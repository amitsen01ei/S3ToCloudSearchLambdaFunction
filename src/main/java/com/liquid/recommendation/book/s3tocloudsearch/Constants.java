package com.liquid.recommendation.book.s3tocloudsearch;

public class Constants {
	public static final String ACCESS_KEY = "AKIAJ5WP5H7HZCERX6LQ";
	public static final String SECRET_KEY = "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";
	public static final String REGION = "ap-northeast-1";

	public static final String BUCKET_NAME = "recommendation-dev";
	public static final String FILE_PATH = "book/summary-analytics/resultset-daily/";

	public static final String SUCCESS_MSG = "SUCCESSFUL";
	public static final String FILE_NOT_FOUND = "FILE NOT FOUND";
	public static final String UPLOAD_FAILED_MSG = "UPLOAD FAILED";

	public static final String QUERY_ALL = "*:*";

	public static final String DATE_FORMAT = "yyyy-MM-dd";

	public static final String CLOUDSEARCH_DOC_ENDPOINT = "doc-recommended-books-roepi3aahn6ktnqp74mohjnttq.ap-northeast-1.cloudsearch.amazonaws.com";
	public static final String CLOUDSEARCH_SEARCH_ENDPOINT = "search-recommended-books-roepi3aahn6ktnqp74mohjnttq.ap-northeast-1.cloudsearch.amazonaws.com";
	public static final String SPLIT_PATTERN = "\r\n|\r|\n";
	public static final String SEPARATOR = ",";

	public static final String CLOUDSEARCH_ADD = "add";
	public static final String CLOUDSEARCH_DELETE = "\"delete\"";
}
