package org.api.weather.apis;

import org.api.common.ApiTestHelper;
import org.api.helper.ApiContext;

public class BaseApi {
	
	private static String apiUrl = null;
    protected static ApiTestHelper helper = new ApiTestHelper();

	protected static String openWeatherApiUrl() {
		apiUrl = ApiContext.INSTANCE.getEntryAsString("OPEN_WEATHER_API_URL");
		if (apiUrl == null) {
			throw new RuntimeException("API URL is incorrect. Please check MainConfig.properties");
		} else {
			return apiUrl;
		}
	}
}
