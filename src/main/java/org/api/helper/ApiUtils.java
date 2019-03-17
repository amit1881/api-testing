package org.api.helper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class ApiUtils {

	public static Properties loadProperties(Class<?> classType, Properties testProperties, String testConfigFileName) {

		try {
			InputStream stream = classType.getClassLoader().getResourceAsStream(testConfigFileName);
			testProperties.load(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testProperties;
	}

	public static Properties loadProperties(Object obj, Properties prop, String configs) {
		return loadProperties(obj.getClass(), prop, configs);
	}

}
