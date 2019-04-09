package org.api.weather.apis;

import java.util.HashMap;
import java.util.TreeMap;

public class WeatherApi extends BaseApi{

	
	public static String getCurrentWeather(String appId, String locationParam) {

        String response = null;
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("APPID", appId);
        params.put("q", locationParam);
        String Url = openWeatherApiUrl() + "/data/2.5/weather";
        try {
            response = helper.sendGetRequest1(Url, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
	
}
