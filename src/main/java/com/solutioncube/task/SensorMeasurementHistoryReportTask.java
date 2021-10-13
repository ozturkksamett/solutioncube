package com.solutioncube.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.solutioncube.common.IProcess;
import com.solutioncube.common.ITask;
import com.solutioncube.common.Task;
import com.solutioncube.dao.SolutionCubeDAO;
import com.solutioncube.helper.CacheManager;
import com.solutioncube.pojo.TaskParameter;

public class SensorMeasurementHistoryReportTask implements ITask, IProcess {

	private static final Logger logger = LoggerFactory.getLogger(SensorMeasurementHistoryReportTask.class);
	private final String BASE_COLLECTION_NAME = "Sensors";
	private final String COLLECTION_NAME = this.getClass().getSimpleName().substring(0,
			this.getClass().getSimpleName().length() - 4);
	private final String URI = "https://api.triomobil.com/facility/v1/reports/sensor/measurements/history?sensorId=%s&ts.since=%s&ts.until=%s&_limit=5000";

	@Override
	public void execute(TaskParameter taskParameter) {

		logger.info("Execution Started");
		List<String> sensors = taskParameter.getMongoTemplate().findAll(String.class, BASE_COLLECTION_NAME);
		logger.info("Sensors Size: " + sensors.size());
		for (String sensor : sensors) {

			JSONObject sensorJSONObject = new JSONObject(sensor);
			String sensorId = sensorJSONObject.getString("_id");
			taskParameter.setId(sensorId);
			taskParameter.setIdColumnName("sensorId");
			taskParameter.setUri(String.format(URI, sensorId, taskParameter.getSinceDateAsString(),
					taskParameter.getTillDateAsString()));
			taskParameter.setCollectionName(COLLECTION_NAME);
			new Task().execute(taskParameter);
			taskParameter.setId(null);
			taskParameter.setIdColumnName(null);
		}
		process(taskParameter.getMongoTemplate());
		logger.info("Execution Done");
	}

	@Override
	public void process(MongoTemplate mongoTemplate) {
		
		logger.info("Process Started");

		List<JSONObject> processedJsonObjects = new ArrayList<JSONObject>();
		
		List<JSONObject> jsonObjects = CacheManager.get(COLLECTION_NAME);
		
		Map<String, List<JSONObject>> groupBySensorIdMap = jsonObjects.stream().collect(Collectors.groupingBy(j -> j.getString("sensorId")));
		
		for (List<JSONObject> sensors : groupBySensorIdMap.values()) {
			
			Comparator<JSONObject> comparator = (c1, c2) -> {
				return LocalDateTime.parse(c1.getString("ts")).compareTo(LocalDateTime.parse(c2.getString("ts")));
			};
			
			Collections.sort(sensors, comparator);

			boolean isStatusChanged = false;
			JSONObject sensor = sensors.get(0);
			int digital2 = sensor.getJSONObject("measurement").getInt("digital2");
			String sts = sensor.getString("ts");
			String ets = sensor.getString("ts");
			for (int i = 1; i < sensors.size(); i++) {

				isStatusChanged = digital2 != sensors.get(i).getJSONObject("measurement").getInt("digital2");
				if(!isStatusChanged) {
					ets = sensors.get(i).getString("ts");
					continue;
				}
				sensor.put("StartDateTime", sts);
				sensor.put("EndDateTime", ets);
				processedJsonObjects.add(sensor);
				sensor = sensors.get(i);
				digital2 = sensor.getJSONObject("measurement").getInt("digital2");
				sts = sensor.getString("ts");
				ets = sensor.getString("ts");
			}				
		}

		SolutionCubeDAO.saveJsonData(mongoTemplate, COLLECTION_NAME + "Processed", processedJsonObjects);
		
		logger.info("Process Done");
	}
}