package com.liquid.recommendation.book.s3tocloudsearch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
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

import com.liquid.recommendation.book.s3tocloudsearch.models.Result;
import com.liquid.recommendation.book.s3tocloudsearch.models.ResultSet;

public class CopyResultSetToCloudSearch implements RequestHandler<Object, String> {

	private BasicAWSCredentials credentials = new BasicAWSCredentials(Constants.ACCESS_KEY, Constants.SECRET_KEY);

	private AWSStaticCredentialsProvider credentialProvider = new AWSStaticCredentialsProvider(credentials);

	private AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(credentialProvider)
			.withRegion(Constants.REGION).build();

	private AmazonCloudSearchDomain domainDocClient = AmazonCloudSearchDomainClientBuilder.standard()
			.withCredentials(credentialProvider)
			.withEndpointConfiguration(new EndpointConfiguration(Constants.CLOUD_SEARCH_DOC_ENDPOINT, Constants.REGION))
			.build();

	private AmazonCloudSearchDomain domainSearchClient = AmazonCloudSearchDomainClientBuilder.standard()
			.withCredentials(credentialProvider).withEndpointConfiguration(
					new EndpointConfiguration(Constants.CLOUD_SEARCH_SEARCH_ENDPOINT, Constants.REGION))
			.build();

	private Pattern pattern = Pattern.compile(Constants.SEPARATOR);

	private ObjectMapper mapper = new ObjectMapper();

	private LambdaLogger logger = null;
	private ByteArrayOutputStream outputStream = null;

	@Override
	public String handleRequest(Object input, Context context) {
		this.logger = context.getLogger();
		log("CopyResultSetToCloudSearch Lambda Started");

		String response = getFileAsStringFromS3();

		if (response != null) {

			convertCsvToJsonOutputStream(response);

			deletePreviousDataFromCloudSearch();

			UploadDocumentsResult result = uploadDocumentToCloudSearch();

			if (result == null) {
				log(Constants.UPLOAD_FAILED_MSG);
			} else {
				log(result.getStatus());
			}

			return Constants.SUCCESS_MSG;
		}
		return Constants.FILE_NOT_FOUND;
	}

	private void deletePreviousDataFromCloudSearch() {

		SearchRequest request = new SearchRequest().withQueryParser(QueryParser.Lucene).withQuery(Constants.QUERY_ALL)
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
		} else {
			log("Domain is empty");
		}
	}

	private UploadDocumentsResult uploadDocumentToCloudSearch() {

		UploadDocumentsRequest request = new UploadDocumentsRequest();
		request.setContentType(ContentType.Applicationjson);
		request.setContentLength((long) outputStream.size());
		request.setDocuments(new ByteArrayInputStream(outputStream.toByteArray()));

		log(request.toString());

		return domainDocClient.uploadDocuments(request);
	}

	private void convertCsvToJsonOutputStream(String csvString) {
		outputStream = new ByteArrayOutputStream();
		String[] lines = csvString.split(Constants.SPLIT_PATTERN);

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

		output.setFields(new Result(values[0], values[1], convertDateTime(values[2]), values[3], values[4], values[5],
				values[6], values[7]));
		output.setType(Constants.CLOUD_SEARCH_ADD);
		output.setId(output.getFields().getBookId() + "-" + output.getFields().getAlgorithm());

		return output;
	}

	private String getFileAsStringFromS3() {
		String filePath = s3Client.listObjects(Constants.BUCKET_NAME, Constants.FILE_PATH + getTodayString() + "/")
				.getObjectSummaries().stream().filter(o -> o.getKey().contains(".csv")).map(S3ObjectSummary::getKey)
				.collect(Collectors.joining(""));

		log("FilePath : " + filePath);
		
		if (filePath.compareTo("") == 0) {
			log("File doesn't exist");
			return null;
		}

		return s3Client.getObjectAsString(Constants.BUCKET_NAME, filePath);
	}

	private String createDeleteQueryItem(List<String> bookIds) {

		StringBuilder deleteQuery = new StringBuilder();
		deleteQuery.append("[").append("{").append("\"type\":").append(Constants.CLOUD_SEARCH_DELETE).append(",")
				.append("\"id\":").append("\"").append(bookIds.get(0)).append("\"").append("}");
		bookIds.remove(0);

		for (String id : bookIds) {
			deleteQuery.append(",").append("{").append("\"type\":").append(Constants.CLOUD_SEARCH_DELETE).append(",")
					.append("\"id\":").append("\"").append(id).append("\"").append("}");
		}
		deleteQuery.append("]");

		return deleteQuery.toString();
	}

	private String convertDateTime(String dateTime) {
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		return DateTimeFormatter.ISO_INSTANT.format(zonedDateTime);
	}

	private String getTodayString() {
		ZonedDateTime today = ZonedDateTime.now();
		return DateTimeFormatter.ofPattern(Constants.DATE_FORMAT).format(today);
	}

	private void log(String text) {
		logger.log(text + "\n");
	}
}
