package com.amazonaws.lambda.s3tocloudsearch;

import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.lambda.s3tocloudsearch.models.Summary;
import com.amazonaws.lambda.s3tocloudsearch.models.SummaryOutput;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomain;
import com.amazonaws.services.cloudsearchdomain.AmazonCloudSearchDomainClientBuilder;
import com.amazonaws.services.cloudsearchdomain.model.ContentType;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsRequest;
import com.amazonaws.services.cloudsearchdomain.model.UploadDocumentsResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class LambdaFunctionHandler implements RequestHandler<Object, String> {

	private BasicAWSCredentials credentials = new BasicAWSCredentials(LambdaConstants.ACCESS_KEY,
			LambdaConstants.SECRET_KEY);
	private AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(LambdaConstants.REGION).build();

	private AmazonCloudSearchDomain cloudSearchClient = AmazonCloudSearchDomainClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withEndpointConfiguration(
					new EndpointConfiguration(LambdaConstants.CLOUDSEARCH_DOC_ENDPOINT, LambdaConstants.REGION))
			.build();

	Pattern pattern = Pattern.compile(LambdaConstants.SEPARATOR);

	ObjectMapper mapper = new ObjectMapper();

	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	@Override
	public String handleRequest(Object input, Context context) {
		context.getLogger().log("Starting Lambda for Fetching file from s3\n");

		String response = getFileAsStringFromS3();

		if (response != null) {
			System.out.println(response);

			convertCsvToJsonOutputStream(response);

			UploadDocumentsResult result = uploadDocumentToCloudSearch();

			outputStream.reset();

			if (result == null) {
				System.out.println("UPLOAD FAILED");
			} else {
				System.out.println(result.getStatus());
			}

			return LambdaConstants.SUCCESS_MSG;
		}
		return LambdaConstants.FILE_NOT_FOUND;
	}
	
	private UploadDocumentsResult uploadDocumentToCloudSearch () {
		UploadDocumentsRequest request = null;

		request = new UploadDocumentsRequest();
		request.setContentType(ContentType.Applicationjson);
		request.setContentLength((long) outputStream.size());
		request.setDocuments(new ByteArrayInputStream(outputStream.toByteArray()));

		System.out.println(request.toString());

		return cloudSearchClient.uploadDocuments(request);
	}

	private void convertCsvToJsonOutputStream(String csvString) {
		String lines[] = csvString.split(LambdaConstants.SPLIT_PATTERN);

		List<SummaryOutput> outputList = Stream.of(lines)
													.skip(1)
													.map(this::convertCsvRowToModel)
													.collect(Collectors.toList());
		
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		try {
			mapper.writeValue(outputStream, outputList);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private SummaryOutput convertCsvRowToModel(String csvRow) {

		String[] values = pattern.split(csvRow);

		SummaryOutput output = new SummaryOutput();

		output.setFields(new Summary(values[0], values[1], values[2], values[3], values[4], values[5], values[6]));
		output.setType(LambdaConstants.CLOUDSEARCH_OPERATION_TYPE);
		output.setId("summary_" + output.getFields().getBook_id());

		return output;
	}

	private String getFileAsStringFromS3() {
		String filePath = LambdaConstants.FILE_PATH + getTodayString() + "/" + getTodayString() + "_summary.csv";
		System.out.println(filePath);

		String response = s3Client.getObjectAsString(LambdaConstants.BUCKET_NAME, filePath);

		return response;
	}

	private String getTodayString() {
//		LocalDateTime today = LocalDateTime.now();
//		return DateTimeFormatter.ofPattern(LambdaConstants.DATE_FORMAT).format(today).toString();
		return "2019-01-28";
	}
}
