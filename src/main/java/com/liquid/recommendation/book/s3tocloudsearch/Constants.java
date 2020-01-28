package com.liquid.recommendation.book.s3tocloudsearch;

class Constants {
	static final String ACCESS_KEY = "ACCESS_KEY";
	static final String SECRET_KEY = "SECRET_KEY";
	static final String REGION = "ap-northeast-1";

	static final String BUCKET_NAME = "recommendation-dev";
	static final String FILE_PATH = "book/summary-analytics/resultset-daily/";

	static final String SUCCESS_MSG = "SUCCESSFUL";
	static final String FILE_NOT_FOUND = "FILE NOT FOUND";
	static final String UPLOAD_FAILED_MSG = "UPLOAD FAILED";

	static final String QUERY_ALL = "*:*";

	static final String DATE_FORMAT = "yyyy-MM-dd";

	static final String CLOUD_SEARCH_DOC_ENDPOINT = "CLOUD_SEARCH_DOC_ENDPOINT";
	static final String CLOUD_SEARCH_SEARCH_ENDPOINT = "CLOUD_SEARCH_SEARCH_ENDPOINT";
	static final String SPLIT_PATTERN = "\r\n|\r|\n";
	static final String SEPARATOR = ",";

	static final String CLOUD_SEARCH_ADD = "add";
	static final String CLOUD_SEARCH_DELETE = "\"delete\"";
}
