package org.api.weather.test.apis;

import static com.jayway.jsonpath.JsonPath.read;
import static org.testng.Assert.assertEquals;

import org.api.common.ApiBaseTest;
import org.api.weather.apis.WeatherApi;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WeatherApiTest extends ApiBaseTest {

	String appId;
	
	@BeforeClass
	public void getData() throws Exception{
		appId = getTestProperty("APP_ID");
	}

	@Test
	public void verifyLocationName() throws Throwable {
		String locationName = getTestProperty("LOCATION_NAME");
		String response = WeatherApi.getCurrentWeather(appId, locationName);
		assertEquals(read(response, "$.name"), "London");
	}

}
