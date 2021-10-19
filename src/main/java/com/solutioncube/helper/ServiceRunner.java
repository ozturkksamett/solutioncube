package com.solutioncube.helper;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.solutioncube.common.IService;
import com.solutioncube.common.ExecutionType;

@Component
public class ServiceRunner {

	private static final Logger logger = LoggerFactory.getLogger(ServiceRunner.class);
	
	@Autowired
	private AsyncHelper asyncHelper;

	public void runServices(List<IService> services, ExecutionType executionType, boolean isAsync) {
		
		logger.info(String.format("%s services started running", services.stream().map(IService::getServiceName).collect(Collectors.joining(", "))));  			
    	Instant start = Instant.now();		
		Collection<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();		
		services.forEach(service -> {
			futures.addAll(service.run(executionType, isAsync));
		});
		asyncHelper.waitTillEndOfSynchronizedFunc(futures);					
		Instant finish = Instant.now();
		logger.info(String.format("Duration: %d minutes", Duration.between(start, finish).toMinutes()));  
	}	
	
	public void runService(IService service, ExecutionType executionType, boolean isAsync) {

		logger.info(String.format("%s service started running", service.getServiceName()));
    	Instant start = Instant.now();
		asyncHelper.waitTillEndOfSynchronizedFunc(service.run(executionType, isAsync));	    	
		Instant finish = Instant.now();
		logger.info(String.format("Duration: %d minutes", Duration.between(start, finish).toMinutes()));
	}	
}