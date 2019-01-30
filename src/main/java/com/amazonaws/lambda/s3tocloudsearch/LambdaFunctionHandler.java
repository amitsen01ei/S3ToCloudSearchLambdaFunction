package com.amazonaws.lambda.s3tocloudsearch;

import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.lambda.s3tocloudsearch.models.Result;
import com.amazonaws.lambda.s3tocloudsearch.models.ResultSet;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomain;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomainClientBuilder;
import com.amazonaws.services.cloudsearchdomain.model.ContentType;
import com.amazonaws.services.cloudsearchdomain.model.Hit;
import com.amazonaws.services.cloudsearchdomain.model.QueryParser;
import com.amazonaws.services.cloudsearchdomain.model.SearchRequest;
import com.amazonaws.services.cloudsearchdomain.model.SearchResult;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsRequest;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class LambdaFunctionHandler implements RequestHandler<Object, String> {

	private BasicAWSCredentials credentials = new BasicAWSCredentials(LambdaConstants.ACCESS_KEY,
			LambdaConstants.SECRET_KEY);

	private AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(LambdaConstants.REGION).build();

	private AmazonCloudSearchDomain domainDocClient = AmazonCloudSearchDomainClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withEndpointConfiguration(
					new EndpointConfiguration(LambdaConstants.CLOUDSEARCH_DOC_ENDPOINT, LambdaConstants.REGION))
			.build();

	private AmazonCloudSearchDomain domainSearchClient = AmazonCloudSearchDomainClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withEndpointConfiguration(
					new EndpointConfiguration(LambdaConstants.CLOUDSEARCH_SEARCH_ENDPOINT, LambdaConstants.REGION))
			.build();

	Pattern pattern = Pattern.compile(LambdaConstants.SEPARATOR);

	ObjectMapper mapper = new ObjectMapper();

	LambdaLogger logger = null;
	ByteArrayOutputStream outputStream = null;

	@Override
	public String handleRequest(Object input, Context context) {
		this.logger = context.getLogger();
		log("Starting Lambda for Fetching file from s3");

		String response = getFileAsStringFromS3();

		if (response != null) {

			convertCsvToJsonOutputStream(response);

			deletePreviousDataFromCloudSearch();

			UploadDocumentsResult result = uploadDocumentToCloudSearch();

			if (result == null) {
				log("UPLOAD FAILED");
			} else {
				log(result.getStatus());
			}

			return LambdaConstants.SUCCESS_MSG;
		}
		return LambdaConstants.FILE_NOT_FOUND;
	}

	private void deletePreviousDataFromCloudSearch() {

		SearchRequest request = new SearchRequest()
								.withQueryParser(QueryParser.Lucene)
								.withQuery("*:*")
								.withSize(10000L);

		SearchResult result = domainSearchClient.search(request);

		List<String> bookIdList = result.getHits().getHit().stream().map(Hit::getId).collect(Collectors.toList());
	
		if (bookIdList.size() != 0) {
			String delete = createDeleteQueryItem(bookIdList);

			UploadDocumentsRequest deleteRequest = new UploadDocumentsRequest();
			deleteRequest.setContentLength((long) delete.length());
			deleteRequest.setContentType(ContentType.Applicationjson);
			deleteRequest.setDocuments(new ByteArrayInputStream(delete.getBytes()));

			log(domainSearchClient.uploadDocuments(deleteRequest).getStatus());
		}
		else {
			log("Domain is empty");
		}
	}

	private UploadDocumentsResult uploadDocumentToCloudSearch() {
		UploadDocumentsRequest request = null;

		request = new UploadDocumentsRequest();
		request.setContentType(ContentType.Applicationjson);
		request.setContentLength((long) outputStream.size());
		request.setDocuments(new ByteArrayInputStream(outputStream.toByteArray()));

		log(request.toString());

		UploadDocumentsResult result = domainDocClient.uploadDocuments(request);

		return result;
	}

	private void convertCsvToJsonOutputStream(String csvString) {
		outputStream = new ByteArrayOutputStream();
		String lines[] = csvString.split(LambdaConstants.SPLIT_PATTERN);

		List<ResultSet> outputList = Stream.of(lines).skip(1).map(String::trim).filter(line -> !line.isEmpty())
				.map(this::convertCsvRowToModel).collect(Collectors.toList());
		
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			mapper.writeValue(outputStream, outputList);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private ResultSet convertCsvRowToModel(String csvRow) {

		String[] values = pattern.split(csvRow);

		ResultSet output = new ResultSet();

		output.setFields(
				new Result(values[0], values[1], convertDateTime(values[2]), values[3], values[4], values[5], values[6], values[7]));
		output.setType(LambdaConstants.CLOUDSEARCH_ADD);
		output.setId(output.getFields().getBookId() + "-" + output.getFields().getAlgorithm());

		return output;
	}

	private String getFileAsStringFromS3() {
		String filePath = s3Client.listObjects(LambdaConstants.BUCKET_NAME, LambdaConstants.FILE_PATH + getTodayString() + "/")
				.getObjectSummaries().stream()
				.filter(o -> o.getKey().contains(".csv"))
				.map(S3ObjectSummary::getKey)
				.collect(Collectors.joining(""));

		log(filePath);

		String response = s3Client.getObjectAsString(LambdaConstants.BUCKET_NAME, filePath);

		return response;
	}

	private String createDeleteQueryItem(List<String> bookIds) {

		StringBuilder deleteQuery = new StringBuilder();
		deleteQuery.append("[").append("{").append("\"type\":").append(LambdaConstants.CLOUDSEARCH_DELETE).append(",")
				.append("\"id\":").append("\"").append(bookIds.get(0)).append("\"").append("}");
		bookIds.remove(0);

		for (String id : bookIds) {
			deleteQuery.append(",").append("{").append("\"type\":").append(LambdaConstants.CLOUDSEARCH_DELETE)
					.append(",").append("\"id\":").append("\"").append(id).append("\"").append("}");
		}
		deleteQuery.append("]");

		return deleteQuery.toString();
	}

	private String convertDateTime (String dateTime) {
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		return DateTimeFormatter.ISO_INSTANT.format(zonedDateTime);
	}

	private String getTodayString () {
		ZonedDateTime today = ZonedDateTime.now();
		return DateTimeFormatter.ofPattern(LambdaConstants.DATE_FORMAT).format(today);
	}

	private void log(String text) {
		logger.log(text + "\n");
	}
}
