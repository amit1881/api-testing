/**
 * 
 */
package org.api.common;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * @author amit
 *
 */
public class ApiTestHelper {

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

	private HttpClient _makeHttpClientInstance() {
		return new DefaultHttpClient();
	}

	private String _createQueryString(Map<String, String> requestMap) {
		List<String> requestParams = new ArrayList<String>();
		for (Entry<String, String> param : requestMap.entrySet()) {
			requestParams.add(param.getKey() + "=" + param.getValue());
		}
		String requestPayload = StringUtils.join(requestParams, "&");
		return requestPayload;
	}

}
