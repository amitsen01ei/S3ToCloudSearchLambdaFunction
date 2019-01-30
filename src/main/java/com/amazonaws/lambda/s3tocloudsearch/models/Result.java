package com.amazonaws.lambda.s3tocloudsearch.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {

	@JsonProperty("book_id")
	private String bookId;

	@JsonProperty("create_time")
	private String createTime;

	private String prefecture;
	private String priority;

	@JsonProperty("total_favorite_count")
	private String totalFavoriteCount;

	@JsonProperty("total_view_count")
	private String totalViewCount;

	@JsonProperty("user_type")
	private String userType;

	private String algorithm;

	public Result(String bookId, String userType, String createTime, String prefecture, String priority, String totalViewCount,
			String totalFavoriteCount, String algorithm) {
		this.bookId = bookId;
		this.createTime = createTime;
		this.prefecture = prefecture;
		this.priority = priority;
		this.totalFavoriteCount = totalFavoriteCount;
		this.totalViewCount = totalViewCount;
		this.userType = userType;
		this.algorithm = algorithm;
	}
	public String getBookId() {
		return bookId;
	}
	public void setBook_id(String bookId) {
		this.bookId = bookId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getPrefecture() {
		return prefecture;
	}
	public void setPrefecture(String prefecture) {
		this.prefecture = prefecture;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getTotalFavoriteCount() {
		return totalFavoriteCount;
	}
	public void setTotal_favorite_count(String totalFavoriteCount) {
		this.totalFavoriteCount = totalFavoriteCount;
	}
	public String getTotalViewCount() {
		return totalViewCount;
	}
	public void setTotal_view_count(String totalViewCount) {
		this.totalViewCount = totalViewCount;
	}
	public String getUserType() {
		return userType;
	}
	public void setUser_type(String userType) {
		this.userType = userType;
	}
	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
}
