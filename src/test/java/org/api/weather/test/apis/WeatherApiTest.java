package org.api.weather.test.apis;

import static com.jayway.jsonpath.JsonPath.read;
import static org.testng.Assert.assertEquals;

import org.api.common.ApiBaseTest;
import org.api.weather.apis.WeatherApi;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WeatherApiTest extends ApiBaseTest {

	String appId;

	String jsonResponse="{\"firstName\":\"John\",\"lastName\":\"doe\",\"age\": 26, "
			+ "\"address\" : {\"streetAddress\": \"naist street\",\"city\": \"Nara\",\"postalCode\":\"630-0192\"}, "
			+ "\"phoneNumbers\": [{\"type\"  : \"iPhone\",\"number\": \"0123-4567-8888\"},"
					+ "{\"type\"  : \"home\",\"number\": \"0123-4567-8910\"}]}";

	@BeforeClass
	public void getData() throws Exception {
		appId = getTestProperty("APP_ID");
	}

	@Test
	public void verifyLocationName() throws Throwable {
		String locationName = getTestProperty("LOCATION_NAME");
		String response = WeatherApi.getCurrentWeather(appId, locationName);
		assertEquals(read(response, "$.name"), "London");
	}

	//@Test
	public void verifyTest2() {
		assertEquals("tst2", "test2");
	}

	//@Test
	public void verifyTest3() {
		assertEquals("tst3", "test3");
	}

	//@Test
	public void verifyTest4() {
		assertEquals("tst4", "test4");
	}
	
	@Test
	public void verifyTest5(){
//		System.out.println(read(jsonResponse,"$.firstName"));
//		//assertEquals(read(jsonResponse,"$.firstName"),"John");
//		System.out.println(read(jsonResponse,"$.address.streetAddress"));
//		System.out.println(read(jsonResponse,"$.address.city"));
//		System.out.println(read(jsonResponse,"$.address.postalCode"));
		//System.out.println(read(jsonResponse,"$.address"));
		System.out.println(read(jsonResponse,"$.phoneNumbers"));
		System.out.println(read(jsonResponse,"$.phoneNumbers[*]"));
		System.out.println(read(jsonResponse,"$.phoneNumbers[0]"));
		System.out.println(read(jsonResponse,"$.phoneNumbers[1]"));
		System.out.println(read(jsonResponse,"$.phoneNumbers[0].type"));
		System.out.println(read(jsonResponse,"$.phoneNumbers[0].number"));
		System.out.println(read(jsonResponse,"$.phoneNumbers[1].type"));
		System.out.println(read(jsonResponse,"$.phoneNumbers[1].number"));
	}

}
