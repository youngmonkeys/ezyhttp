package com.tvd12.ezyhttp.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import com.tvd12.ezyhttp.client.request.RequestEntity;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.Headers;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.response.ResponseEntity;

public class HttpClient {

	public <T> ResponseEntity<T> request(
			HttpMethod method, 
			String url, 
			RequestEntity entity, 
			Class<T> responseType, 
			int connectTimeout, int readTimeout) throws Exception {
		URL requestURL = new URL(url);
		HttpURLConnection connection = (HttpsURLConnection)requestURL.openConnection();
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);
		connection.setDoOutput(true);
		Map<String, String> requestHeaders = entity.getHeaders();
		if(requestHeaders != null) {
			for(Entry<String, String> requestHeader : requestHeaders.entrySet())
				connection.setRequestProperty(requestHeader.getKey(), requestHeader.getValue());
		}
		String requestContentType = connection.getRequestProperty(Headers.CONTENT_TYPE);
		if(requestContentType == null)
			connection.setRequestProperty(Headers.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
		byte[] requestBytes = serializeRequestBody(entity);
		OutputStream outputStream = connection.getOutputStream();
		outputStream.write(requestBytes);
		
		int responseCode = connection.getResponseCode();
		Map<String, List<String>> headerFields = connection.getHeaderFields();
		InputStream inputStream = connection.getInputStream();
		T responseBody = deserializeResponseBody(inputStream, responseType);
		return new ResponseEntity<T>(responseCode, null, responseBody);
	}
	
	protected byte[] serializeRequestBody(RequestEntity entity) {
		return null;
	}
	
	protected <T> T deserializeResponseBody(InputStream inputStream, Class<T> responseType) {
		return null;
	}
	
}
