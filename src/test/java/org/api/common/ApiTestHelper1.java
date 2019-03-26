/**
 * 
 */
package org.api.common;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;

/**
 * @author amit
 *
 */
public class ApiTestHelper1 {

	private static final long MILLIS_IN_SECOND = 1000;
	private static final String SEPARATOR = "\n";
	private static final String UTF_8 = "UTF-8";
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	private ObjectMapper objectMapper;

	/**
	 * 
	 */
	public ApiTestHelper1() {
		objectMapper = new ObjectMapper();
		objectMapper.setVisibility(JsonMethod.FIELD, Visibility.ANY).setVisibility(JsonMethod.SETTER, Visibility.NONE)
				.setVisibility(JsonMethod.GETTER, Visibility.NONE).setVisibility(JsonMethod.IS_GETTER, Visibility.NONE)
				.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(SerializationConfig.Feature.AUTO_DETECT_GETTERS, false)
				.configure(SerializationConfig.Feature.AUTO_DETECT_IS_GETTERS, false);
	}

	public long getUnixTimestampGmt() {
		long millis = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
		long currentUnixTimestamp = millis / MILLIS_IN_SECOND;
		return currentUnixTimestamp;
	}

	/**
	 * Converts Json to a map
	 * 
	 * @param json
	 * @return
	 */
	public Map<String, Object> convertJsonToMap(String json) {
		try {
			TypeReference<HashMap<String, Object>> typeReferenceMapObject = new TypeReference<HashMap<String, Object>>() {
			};

			Map<String, Object> map = objectMapper.readValue(json, typeReferenceMapObject);
			return map;
		} catch (Exception e) {
			throw new RuntimeException("Server Error - Could not convert Json to Map", e);
		}
	}

	public String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			throw new RuntimeException("Json conversion failure - Received object is not in valid format - " + object,
					e);
		}
	}

	public String makeSignatureAndEncodeParametersValues(TreeMap<String, String> parameters, String apiUrl,
			String requestMethod, String privateKey)
			throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {

		String concatenatedString = _makeConcatenatedString(parameters, apiUrl, requestMethod);
		String signature = _generateSignature(concatenatedString, privateKey);
		for (Entry<String, String> parameter : parameters.entrySet()) {
			parameter.setValue(URLEncoder.encode(parameter.getValue(), UTF_8));
		}
		return signature;
	}

	public String makeSignatureAndDontEncodeParams(TreeMap<String, String> parameters, String apiUrl,
			String requestMethod, String privateKey)
			throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {

		String concatenatedString = _makeConcatenatedString(parameters, apiUrl, requestMethod);
		String signature = _generateSignature(concatenatedString, privateKey);
		return signature;
	}

	public String sendGetRequest(String apiUrl, Map<String, String> requestMap) throws URISyntaxException {
		String jsonResponse = null;
		HttpResponse httpResponse = null;

		String queryString = this._createQueryString(requestMap);
		URI uri = URI.create(apiUrl + "?" + queryString);

		HttpGet httpGet = null;
		try {
			HttpClient httpClient = _makeHttpClientInstance();

			httpGet = new HttpGet(uri);
			httpResponse = httpClient.execute(httpGet);
			if (httpResponse == null || (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK
					&& httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_BAD_REQUEST))
				throw new RuntimeException("Communication Response - Received Resposne From server with Status="
						+ httpResponse.getStatusLine());

			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				jsonResponse = EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			throw new RuntimeException("Unexpected Error", e);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null)
				try {
					httpResponse.getEntity().getContent().close();
				} catch (Exception e) {
				}
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}
		return jsonResponse;
	}

	public String sendGetRequestForPagination(String apiUrl) throws URISyntaxException {
		String jsonResponse = null;
		HttpResponse httpResponse = null;

		URI uri = URI.create(apiUrl);

		HttpGet httpGet = null;
		try {
			HttpClient httpClient = _makeHttpClientInstance();

			httpGet = new HttpGet(uri);
			httpResponse = httpClient.execute(httpGet);
			if (httpResponse == null || httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				throw new RuntimeException("Communication Response - Received Resposne From server with Status="
						+ httpResponse.getStatusLine());

			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				jsonResponse = EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			throw new RuntimeException("Unexpected Error", e);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null)
				try {
					httpResponse.getEntity().getContent().close();
				} catch (Exception e) {
				}
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}
		return jsonResponse;
	}

	public String sendPostRequest(String apiUrl, Map<String, String> requestMap) throws URISyntaxException {
		String jsonResponse = null;
		HttpResponse httpResponse = null;

		URI uri = URI.create(apiUrl);

		HttpPost httpPost = null;
		try {
			HttpClient httpClient = _makeHttpClientInstance();

			String queryString = _createQueryString(requestMap);
			StringEntity payLoad = new StringEntity(queryString,
					ContentType.create("application/x-www-form-urlencoded", "UTF-8"));

			httpPost = new HttpPost(uri);
			httpPost.setEntity(payLoad);

			httpResponse = httpClient.execute(httpPost);
			if (httpResponse == null || httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				throw new RuntimeException("Communication Response - Received Resposne From server with Status="
						+ httpResponse.getStatusLine());

			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				jsonResponse = EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected Error", e);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null)
				try {
					httpResponse.getEntity().getContent().close();
				} catch (Exception e) {
				}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		return jsonResponse;
	}

	public String sendPostRequestForImage(String apiUrl, Map<String, String> requestMap, Map<String, File> files)
			throws URISyntaxException {
		String jsonResponse = null;
		HttpResponse httpResponse = null;

		URI uri = URI.create(apiUrl);

		HttpPost httpPost = null;
		try {
			HttpClient httpClient = _makeHttpClientInstance();

			// String queryString = _createQueryString(requestMap);
			// StringEntity payLoad = new StringEntity(queryString,
			// ContentType.create("application/x-www-form-urlencoded",
			// "UTF-8"));

			MultipartEntityBuilder mpeBuilder = MultipartEntityBuilder.create();
			mpeBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

			for (Entry<String, File> entry : files.entrySet()) {
				mpeBuilder.addPart(entry.getKey(), new FileBody(entry.getValue()));
			}

			for (Entry<String, String> entry : requestMap.entrySet()) {
				mpeBuilder.addPart(entry.getKey(),
						new StringBody(URLDecoder.decode(entry.getValue(), "UTF-8"), ContentType.TEXT_PLAIN));
			}

			HttpEntity content = mpeBuilder.build();

			httpPost = new HttpPost(uri);

			httpPost.setEntity(content);

			httpResponse = httpClient.execute(httpPost);
			if (httpResponse == null || httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				throw new RuntimeException("Communication Response - Received Resposne From server with Status="
						+ httpResponse.getStatusLine());

			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				jsonResponse = EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected Error", e);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null)
				try {
					httpResponse.getEntity().getContent().close();
				} catch (Exception e) {
				}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		return jsonResponse;
	}

	public String sendDeleteRequest(String apiUrl, Map<String, String> requestMap) throws URISyntaxException {
		String jsonResponse = null;
		HttpResponse httpResponse = null;

		String queryString = this._createQueryString(requestMap);
		URI uri = URI.create(apiUrl + "?" + queryString);

		HttpDelete httpDelete = null;
		try {
			HttpClient httpClient = _makeHttpClientInstance();

			httpDelete = new HttpDelete(uri);
			httpResponse = httpClient.execute(httpDelete);
			if (httpResponse == null || httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				throw new RuntimeException("Communication Response - Received Resposne From server with Status="
						+ httpResponse.getStatusLine());

			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				jsonResponse = EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			throw new RuntimeException("Unexpected Error", e);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null)
				try {
					httpResponse.getEntity().getContent().close();
				} catch (Exception e) {
				}
			if (httpDelete != null) {
				httpDelete.releaseConnection();
			}
		}
		return jsonResponse;
	}

	public String sendPostRequestWithPayLoad(String apiUrl, Map<String, String> requestMap) throws URISyntaxException {
		String jsonResponse = null;
		HttpResponse httpResponse = null;

		URI uri = URI.create(apiUrl);

		HttpPost httpPost = null;
		try {
			HttpClient httpClient = _makeHttpClientInstance();
			List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
			for (Entry<String, String> parameter : requestMap.entrySet()) {
				nvps.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
			}

			httpPost = new HttpPost(uri);
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

			httpResponse = httpClient.execute(httpPost);

			if (httpResponse == null || httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				throw new RuntimeException("Communication Response - Received Resposne From server with Status="
						+ httpResponse.getStatusLine());

			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				jsonResponse = EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected Error", e);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null)
				try {
					httpResponse.getEntity().getContent().close();
				} catch (Exception e) {
				}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		return jsonResponse;
	}

	private HttpClient _makeHttpClientInstance() {
		return new DefaultHttpClient();
	}

	private String _makeConcatenatedString(TreeMap<String, String> parameters, String apiUrl, String requestMethod) {
		StringBuilder builder = new StringBuilder();
		builder.append(requestMethod).append(apiUrl);

		for (Entry<String, String> parameter : parameters.entrySet()) {
			builder.append(SEPARATOR).append(parameter.getValue());
		}

		String cString = builder.toString();
		return cString;
	}

	private String _generateSignature(String concatenatedString, String privateKey)
			throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		String algorithm = HMAC_SHA1_ALGORITHM;
		byte[] keyBytes = privateKey.getBytes();
		Key key = new SecretKeySpec(keyBytes, 0, keyBytes.length, algorithm);
		Mac mac = Mac.getInstance(algorithm);
		mac.init(key);
		byte[] doFinal = mac.doFinal(concatenatedString.getBytes());
		String base64String = new String(Base64.encodeBase64(doFinal));
		String signature = base64String.replaceAll("\r", "").replaceAll("\n", "");
		signature = URLEncoder.encode(signature, UTF_8);
		return signature;
	}

	private String _createQueryString(Map<String, String> requestMap) {
		List<String> requestParams = new ArrayList<String>();
		for (Entry<String, String> param : requestMap.entrySet()) {
			requestParams.add(param.getKey() + "=" + param.getValue());
		}
		String requestPayload = StringUtils.join(requestParams, "&");
		return requestPayload;
	}

	public String sendPostRequestWithJsonParametersInBody(String apiUrl, String requestJson) throws URISyntaxException {
		String jsonResponse = null;
		HttpResponse httpResponse = null;

		URI uri = URI.create(apiUrl);

		HttpPost httpPost = null;
		try {
			HttpClient httpClient = _makeHttpClientInstance();

			StringEntity payLoad = new StringEntity(requestJson, ContentType.create("application/json", "UTF-8"));

			httpPost = new HttpPost(uri);
			httpPost.setEntity(payLoad);

			httpResponse = httpClient.execute(httpPost);
			if (httpResponse == null || httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				throw new RuntimeException("Communication Response - Received Resposne From server with Status="
						+ httpResponse.getStatusLine());

			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				jsonResponse = EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unexpected Error", e);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null)
				try {
					httpResponse.getEntity().getContent().close();
				} catch (Exception e) {
				}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		return jsonResponse;
	}

}
