package com.amazonaws.lambda.s3tocloudsearch.models;

public class ResultSet {

	private String type;
	private String id;
	private Result fields;

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
	public Result getFields() {
		return fields;
	}
	public void setFields(Result fields) {
		this.fields = fields;
	}
}
