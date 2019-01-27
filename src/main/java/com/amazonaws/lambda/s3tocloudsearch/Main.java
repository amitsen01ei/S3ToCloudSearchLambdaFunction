package com.amazonaws.lambda.s3tocloudsearch;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
	
	private static final String DATE_FORMAT = "YYYY-MM-DD";

	public static void main(String[] args) {
		System.out.println(getTodayString());
		String filePath = LambdaConstants.FILE_PATH + getTodayString() +"/" + getTodayString() + "_summary.csv";
		System.out.println(filePath);
		
		String response = "book_id,create_time,prefecture,priority,total_favorite_count,total_view_count,user_type\n" + 
				"3,2019-01-27T20:18:00Z,TOKYO,2,67,864,FREE_LANCE\n" + 
				"4,2019-01-27T20:18:00Z,LONDON,5,78,1658,BITO_BITO";
	}
	
	private static String getTodayString () {
		ZonedDateTime today = ZonedDateTime.now();
		return DateTimeFormatter.ofPattern(DATE_FORMAT).format(today).toString();
	}

}
