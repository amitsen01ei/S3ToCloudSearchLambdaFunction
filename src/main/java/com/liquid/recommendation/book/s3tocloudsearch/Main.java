package com.liquid.recommendation.book.s3tocloudsearch;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class Main {

	private static final String DATE_FORMAT = "YYYY-MM-DD";
	private static final String dateString = "2019-01-22T19:06:43.000+06:00";
	
	private static String data = "c068c230-2a97-11e9-bdcc-3f284732efbd,ef7f3660-65d3-4781-86f6-5e8d08d3f513,2019-02-07T05:18:11.716Z;c1779a20-2a97-11e9-bdcc-3f284732efbd,ef7f3660-65d3-4781-86f6-5e8d08d3f513,2019-02-07T05:18:11.716Z;c1b31c80-2a97-11e9-bdcc-3f284732efbd,ef7f3660-65d3-4781-86f6-5e8d08d3f513,2019-02-07T05:18:11.716Z\n" + 
			"51c61150-2ad0-11e9-babb-1d8c4a86530f,e45fa101-598c-4d0f-b516-9d1bf0068f1a,2019-02-07T12:03:06.050Z;a0d08fff-f22e-459c-9134-50549764eecf,e45fa101-598c-4d0f-b516-9d1bf0068f1a,2019-02-07T12:03:06.051Z;6008c7d7-9c6f-4ee8-ba42-5348760e04df,e45fa101-598c-4d0f-b516-9d1bf0068f1a,2019-02-07T12:03:06.051Z\n" + 
			"51c61150-2ad0-11e9-babb-1d8c4a86530f,8830e275-23cd-49ed-b706-f6c4083d7a97,2019-02-08T06:04:39.090Z;518c901e-876f-4b7e-9dec-6f7aaa06664c,8830e275-23cd-49ed-b706-f6c4083d7a97,2019-02-08T06:04:39.090Z;c39cfc70-2aea-11e9-a632-25278547bfd2,8830e275-23cd-49ed-b706-f6c4083d7a97,2019-02-08T06:04:39.090Z\n" + 
			"f9d39260-2acf-11e9-babb-1d8c4a86530f,df313dda-09ad-4f66-b58d-5ab52ac4738e,2019-02-07T12:00:40.708Z;fac78f50-2acf-11e9-babb-1d8c4a86530f,df313dda-09ad-4f66-b58d-5ab52ac4738e,2019-02-07T12:00:40.708Z;fb0ad9e0-2acf-11e9-babb-1d8c4a86530f,df313dda-09ad-4f66-b58d-5ab52ac4738e,2019-02-07T12:00:40.708Z\n" + 
			"7d760050-2a01-11e9-abed-ab7283f8d584,3bdb8ff8-26d8-404a-baa9-65b429e21cf8,2019-02-06T11:22:34.252Z\n" + 
			"eb66b9f0-2a02-11e9-abed-ab7283f8d584,7c8678fd-8e9b-4d45-bcd4-518518612246,2019-02-06T11:32:47.157Z;eb17d6a0-2a02-11e9-abed-ab7283f8d584,7c8678fd-8e9b-4d45-bcd4-518518612246,2019-02-06T11:32:47.157Z;ea4878b0-2a02-11e9-abed-ab7283f8d584,7c8678fd-8e9b-4d45-bcd4-518518612246,2019-02-06T11:32:47.157Z\n" + 
			"51c61150-2ad0-11e9-babb-1d8c4a86530f,d91f81c7-308a-47cd-ab73-6ac0a559df0c,2019-02-08T06:03:01.012Z;518c901e-876f-4b7e-9dec-6f7aaa06664c,d91f81c7-308a-47cd-ab73-6ac0a559df0c,2019-02-08T06:03:01.012Z;c39cfc70-2aea-11e9-a632-25278547bfd2,d91f81c7-308a-47cd-ab73-6ac0a559df0c,2019-02-08T06:03:01.012Z";

	public static void main(String[] args) {
		String[] rows = data.split(";");
		Stream.of(rows).forEach(System.out::println);
	}

	private static String getTodayString () {
		ZonedDateTime today = ZonedDateTime.now();
		return DateTimeFormatter.ofPattern(DATE_FORMAT).format(today);
	}
}