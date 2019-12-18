package com.tvd12.ezyhttp.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.client.request.Request;
import com.tvd12.ezyhttp.client.request.RequestEntity;
import com.tvd12.ezyhttp.core.codec.BodyDeserializer;
import com.tvd12.ezyhttp.core.codec.BodySerializer;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.Headers;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.data.MultiValueMap;
import com.tvd12.ezyhttp.core.response.ResponseEntity;

public class HttpClient {

	protected final int defatReadTimeout;
	protected final int defaultConnectTimeout;
	protected final DataConverters dataConverters;
	
	public static final int NO_TIMEOUT = -1;
	
	protected HttpClient(Builder builder) {
		this.defatReadTimeout = builder.readTimeout;
		this.defaultConnectTimeout = builder.connectTimeout;
		this.dataConverters = builder.dataConverters;
	}
	
	public <T> T call(Request request) throws Exception {
		return call(
				request.getMethod(),
				request.getURL(),
				request.getEntity(),
				request.getResponseType(),
				request.getConnectTimeout(),
				request.getReadTimeout()
		);
	}
	
	public <T> T call(HttpMethod method, 
			String url, 
			RequestEntity entity, 
			Class<T> responseType, 
			int connectTimeout, int readTimeout) throws Exception {
		ResponseEntity<T> responseEntity = request(
				method, 
				url, 
				entity, 
				responseType, 
				connectTimeout, readTimeout);
		return responseEntity.getBody();
	}
	
	public <T> ResponseEntity<T> request(Request request) throws Exception {
		return request(
				request.getMethod(),
				request.getURL(),
				request.getEntity(),
				request.getResponseType(),
				request.getConnectTimeout(),
				request.getReadTimeout()
		);
	}
	
	public <T> ResponseEntity<T> request(
			HttpMethod method, 
			String url, 
			RequestEntity entity, 
			Class<T> responseType, 
			int connectTimeout, int readTimeout) throws Exception {
		HttpURLConnection connection = connect(url);
		try {
			connection.setConnectTimeout(connectTimeout > 0 ? connectTimeout : defaultConnectTimeout);
			connection.setReadTimeout(readTimeout > 0 ? readTimeout : defatReadTimeout);
			connection.setDoOutput(true);
			connection.setRequestMethod(method.toString());
			MultiValueMap requestHeaders = entity != null ? entity.getHeaders() : null;
			if(requestHeaders != null) {
				Map<String, String> encodedHeaders = requestHeaders.toMap();
				for(Entry<String, String> requestHeader : encodedHeaders.entrySet())
					connection.setRequestProperty(requestHeader.getKey(), requestHeader.getValue());
			}
			Object requestBody = entity != null ? entity.getBody() : null;
			if(requestBody != null) {
				String requestContentType = connection.getRequestProperty(Headers.CONTENT_TYPE);
				if(requestContentType == null) {
					requestContentType = ContentTypes.APPLICATION_JSON;
					connection.setRequestProperty(Headers.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
				}
				byte[] requestBytes = serializeRequestBody(requestContentType, requestBody);
				OutputStream outputStream = connection.getOutputStream();
				outputStream.write(requestBytes);
				outputStream.flush();
				outputStream.close();
			}
			
			int responseCode = connection.getResponseCode();
			Map<String, List<String>> headerFields = connection.getHeaderFields();
			MultiValueMap responseHeaders = MultiValueMap.of(headerFields);
			String responseContentType = responseHeaders.getValue(Headers.CONTENT_TYPE);
			if(responseContentType == null)
				responseContentType = ContentTypes.APPLICATION_JSON;
			InputStream inputStream = connection.getInputStream();
			T responseBody = null;
			try {
				responseBody = deserializeResponseBody(responseContentType, inputStream, responseType);
			}
			finally {
				inputStream.close();
			}
			return new ResponseEntity<T>(responseCode, responseHeaders, responseBody);
		}
		finally {
			connection.disconnect();
		}
	}
	
	public HttpURLConnection connect(String url) throws Exception {
		URL requestURL = new URL(url);
		HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
		return connection;
	}
	
	protected byte[] serializeRequestBody(
			String contentType, Object requestBody) throws IOException {
		BodySerializer serializer = dataConverters.getBodySerializer(contentType);
		if(serializer == null)
			throw new IOException("has no serializer for: " + contentType);
		byte[] bytes = serializer.serialize(requestBody);
		return bytes;
	}
	
	protected <T> T deserializeResponseBody(
			String contentType,
			InputStream inputStream, Class<T> responseType) throws IOException {
		BodyDeserializer deserializer = dataConverters.getBodyDeserializer(contentType);
		if(deserializer == null)
			throw new IOException("has no deserializer for: " + contentType);
		T body = deserializer.deserialize(inputStream, responseType);
		return body;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder implements EzyBuilder<HttpClient> {
		
		protected int readTimeout;
		protected int connectTimeout;
		protected DataConverters dataConverters;
		
		public Builder() {
			this.readTimeout = 15 * 1000;
			this.connectTimeout = 15 * 1000;
			this.dataConverters = new DataConverters();
		}
		
		public Builder readTimeout(int readTimeout) {
			this.readTimeout = readTimeout;
			return this;
		}
		
		public Builder connectTimeout(int connectTimeout) {
			this.connectTimeout = connectTimeout;
			return this;
		}
		
		public Builder setStringConverter(Object converter) {
			this.dataConverters.setStringConverter(converter);
			return this;
		}
		
		public Builder addBodyConverter(Object converter) {
			this.dataConverters.addBodyConverter(converter);
			return this;
		}
		
		public Builder addBodyConverters(List<?> converters) {
			this.dataConverters.addBodyConverters(converters);
			return this;
		}
		
		@Override
		public HttpClient build() {
			return new HttpClient(this);
		}
	}
	
}
