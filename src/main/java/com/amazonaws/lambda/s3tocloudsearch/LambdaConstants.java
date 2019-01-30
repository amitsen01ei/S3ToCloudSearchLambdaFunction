package com.amazonaws.lambda.s3tocloudsearch;

public class LambdaConstants {
	
	public static final String ACCESS_KEY = "AKIAIZ6H6XVWYGEGKYCQ";
	public static final String SECRET_KEY = "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";
	public static final String REGION = "ap-northeast-1";
	
	public static final String BUCKET_NAME = "recommendation-sagar";
	public static final String FILE_PATH = "book/summary-analytics/result-set/";
	
	public static final String SUCCESS_MSG = "SUCCESSFUL";
	public static final String FILE_NOT_FOUND = "FILE NOT FOUND";
	
	public static final String DATE_FORMAT = "YYYY-MM-DD";
	
	public static final String CLOUDSEARCH_DOC_ENDPOINT = "doc-recommendation-collection-dgkyrvsvr3jucatwd4f5pbmxjq.ap-northeast-1.cloudsearch.amazonaws.com";
	public static final String CLOUDSEARCH_SEARCH_ENDPOINT = "search-recommendation-collection-dgkyrvsvr3jucatwd4f5pbmxjq.ap-northeast-1.cloudsearch.amazonaws.com";
	public static final String SPLIT_PATTERN = "\r\n|\r|\n";
	public static final String SEPARATOR = ",";
	
	public static final String CLOUDSEARCH_ADD = "add";
	public static final String CLOUDSEARCH_DELETE = "\"delete\"";
}
