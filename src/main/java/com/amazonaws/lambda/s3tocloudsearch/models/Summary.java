package com.amazonaws.lambda.s3tocloudsearch.models;

public class Summary {
	
	private String book_id;
	private String create_time;
	private String prefecture;
	private String priority;
	private String total_favorite_count;
	private String total_view_count;
	private String user_type;
	
	public Summary(String book_id, String create_time, String prefecture, String priority, String total_favorite_count,
			String total_view_count, String user_type) {
		this.book_id = book_id;
		this.create_time = create_time;
		this.prefecture = prefecture;
		this.priority = priority;
		this.total_favorite_count = total_favorite_count;
		this.total_view_count = total_view_count;
		this.user_type = user_type;
	}
	public String getBook_id() {
		return book_id;
	}
	public void setBook_id(String book_id) {
		this.book_id = book_id;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
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
	public String getTotal_favorite_count() {
		return total_favorite_count;
	}
	public void setTotal_favorite_count(String total_favorite_count) {
		this.total_favorite_count = total_favorite_count;
	}
	public String getTotal_view_count() {
		return total_view_count;
	}
	public void setTotal_view_count(String total_view_count) {
		this.total_view_count = total_view_count;
	}
	public String getUser_type() {
		return user_type;
	}
	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}
}
