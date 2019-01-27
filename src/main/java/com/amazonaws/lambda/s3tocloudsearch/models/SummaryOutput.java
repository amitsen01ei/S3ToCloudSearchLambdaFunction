package com.amazonaws.lambda.s3tocloudsearch.models;

public class SummaryOutput {
	
	private String type;
	private String id;
	private Summary fields;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Summary getFields() {
		return fields;
	}
	public void setFields(Summary fields) {
		this.fields = fields;
	}
}
