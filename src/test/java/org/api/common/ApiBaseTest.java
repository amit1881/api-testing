package org.api.common;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import org.api.helper.ApiContext;
import org.api.helper.ApiUtils;

public abstract class ApiBaseTest {

	private Properties testProperties;
	protected ApiTestHelper helper;
	Logger log = Logger.getLogger(ApiBaseTest.class.getName());

	/******************************* SETUP *********************/

	@BeforeSuite
	public void initializeSuite() throws IOException {
		Properties mainConfigProperties = new Properties();
		mainConfigProperties.load(this.getClass().getClassLoader().getResourceAsStream("config/MainConfig.properties"));
		ApiContext.INSTANCE.loadFromProperties(mainConfigProperties);

		Reporter.log("Test Suite Initialized with the following properties", true);
		Reporter.log("****************************************************", true);
		String key = "* " + "testEnvironment" + " -> " + ApiContext.INSTANCE.getEntryAsString("testEnvironment");
		Reporter.log(key, true);
		Reporter.log("****************************************************", true);
	}

	@BeforeClass
	public void beforeClass() throws Exception {
		testProperties = new Properties();
		String testConfigFileName = this.getClass().getSimpleName();
		Reporter.log("Loading Test Configuration from: " + testConfigFileName + "....", true);

		try {
			testProperties = ApiUtils.loadProperties(this,testProperties,testConfigFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeMethod
	public void init(Method method) {
		System.out.println("\t* " + method.getName());
		log.info(" ");
		log.info("====================================" + method.getName() + "=======================================");
		log.info(" ");
		helper = new ApiTestHelper();
	}

	/**************************** USEFUL METHODS *************************/

	protected String getTestProperty(String key) throws Exception {
		try {
			return testProperties.getProperty(key);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
