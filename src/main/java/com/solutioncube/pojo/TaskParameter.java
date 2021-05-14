package com.solutioncube.pojo;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.solutioncube.common.TokenGenerator;

public class TaskParameter {
	
	private Firm firm;

	private MongoTemplate mongoTemplate;

	private LocalDateTime now;

	private String token;

	private LocalDateTime sinceDate;

	private String id;

	private String idColumnName;

	private String uri;

	private String collectionName;

	public TaskParameter(Firm firm, MongoTemplate mongoTemplate, int interval) {

		setFirm(firm);
		setMongoTemplate(mongoTemplate);
		setNow(LocalDateTime.now());
		setSinceDate(now.minusMinutes(interval));
		setToken(generateToken());
	}

	public LocalDateTime getNow() {
		return now;
	}

	public void setNow(LocalDateTime now) {
		this.now = now;
	}

	public Firm getFirm() {
		return firm;
	}

	public void setFirm(Firm firm) {
		this.firm = firm;
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getSinceDate() {
		return sinceDate;
	}

	public void setSinceDate(LocalDateTime sinceDate) {
		this.sinceDate = sinceDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdColumnName() {
		return idColumnName;
	}

	public void setIdColumnName(String idColumnName) {
		this.idColumnName = idColumnName;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getSinceDateAsString() {
		return formattedDateAsString(sinceDate);
	}

	public String getNowAsString() {
		return formattedDateAsString(now);
	}

	private String formattedDateAsString(LocalDateTime date) {

		return date.getYear() + "-" + String.format("%02d", date.getMonthValue()) + "-"
				+ String.format("%02d", date.getDayOfMonth()) + "T" + String.format("%02d", date.getHour()) + "%3A"
				+ String.format("%02d", date.getMinute()) + "%3A" + String.format("%02d", date.getSecond())
				+ ".000%2B03%3A00";
	}

	public String generateToken() {
		if (firm == null)
			return null;
		String username = firm.getUsername();
		String password = firm.getPassword();
		return TokenGenerator.generateToken(username, password);
	}
	
	@Override
	public String toString(){
		
		return new StringBuilder()
		        .append(" Firm: " + this.firm.toString())
		        .append(" CollectionName: " + this.collectionName.toString())
		        .append(" Uri: " + this.uri.toString())
		        .append(" Token: " + this.token.toString())
		        .toString();
	}
}