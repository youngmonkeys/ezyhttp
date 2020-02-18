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
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.data.MultiValueMap;
import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;
import com.tvd12.ezyhttp.core.exception.HttpConflictException;
import com.tvd12.ezyhttp.core.exception.HttpForbiddenException;
import com.tvd12.ezyhttp.core.exception.HttpInternalServerErrorException;
import com.tvd12.ezyhttp.core.exception.HttpMethodNotAllowedException;
import com.tvd12.ezyhttp.core.exception.HttpNotAcceptableException;
import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import com.tvd12.ezyhttp.core.exception.HttpRequestException;
import com.tvd12.ezyhttp.core.exception.HttpRequestTimeoutException;
import com.tvd12.ezyhttp.core.exception.HttpUnauthorizedException;
import com.tvd12.ezyhttp.core.exception.HttpUnsupportedMediaTypeException;
import com.tvd12.ezyhttp.core.response.ResponseEntity;

public class HttpClient {

	protected final int defatReadTimeout;
	protected final int defaultConnectTimeout;
//	protected final CookieManager cookieManager;
	protected final DataConverters dataConverters;
	
	public static final int NO_TIMEOUT = -1;
	
	protected HttpClient(Builder builder) {
		this.defatReadTimeout = builder.readTimeout;
		this.defaultConnectTimeout = builder.connectTimeout;
		this.dataConverters = builder.dataConverters;
	}
	
	public <T> T call(Request request) throws Exception {
		ResponseEntity response = request(
				request.getMethod(),
				request.getURL(),
				request.getEntity(),
				request.getResponseTypes(),
				request.getConnectTimeout(),
				request.getReadTimeout()
		);
		return getResponseBody(response);
	}
	
	public ResponseEntity request(Request request) throws Exception {
		return request(
				request.getMethod(),
				request.getURL(),
				request.getEntity(),
				request.getResponseTypes(),
				request.getConnectTimeout(),
				request.getReadTimeout()
		);
	}
	
	public ResponseEntity request(
			HttpMethod method, 
			String url, 
			RequestEntity entity, 
			Map<Integer, Class<?>> responseTypes, 
			int connectTimeout, int readTimeout) throws Exception {
		HttpURLConnection connection = connect(url);
		try {
			connection.setConnectTimeout(connectTimeout > 0 ? connectTimeout : defaultConnectTimeout);
			connection.setReadTimeout(readTimeout > 0 ? readTimeout : defatReadTimeout);
			connection.setRequestMethod(method.toString());
			connection.setDoInput(true);
			connection.setDoOutput(method.hasOutput());
			connection.setInstanceFollowRedirects(method == HttpMethod.GET);
			MultiValueMap requestHeaders = entity != null ? entity.getHeaders() : null;
			if(requestHeaders != null) {
				Map<String, String> encodedHeaders = requestHeaders.toMap();
				for(Entry<String, String> requestHeader : encodedHeaders.entrySet())
					connection.setRequestProperty(requestHeader.getKey(), requestHeader.getValue());
			}
			Object requestBody = null;
			if(method != HttpMethod.GET && entity != null) {
				requestBody = entity.getBody();
			}
			byte[] requestBodyBytes = null;
			if(requestBody != null) {
				String requestContentType = connection.getRequestProperty(Headers.CONTENT_TYPE);
				if(requestContentType == null) {
					requestContentType = ContentTypes.APPLICATION_JSON;
					connection.setRequestProperty(Headers.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
				}
				requestBodyBytes = serializeRequestBody(requestContentType, requestBody);
				int requestContentLength = requestBodyBytes.length;
				connection.setFixedLengthStreamingMode(requestContentLength);
			}
			
			connection.connect();
			
			if(requestBody != null) {
				OutputStream outputStream = connection.getOutputStream();
				outputStream.write(requestBodyBytes);
				outputStream.flush();
				outputStream.close();
			}
			
			int responseCode = connection.getResponseCode();
			Map<String, List<String>> headerFields = connection.getHeaderFields();
			MultiValueMap responseHeaders = MultiValueMap.of(headerFields);
			String responseContentType = responseHeaders.getValue(Headers.CONTENT_TYPE);
			if(responseContentType == null)
				responseContentType = ContentTypes.APPLICATION_JSON;
			InputStream inputStream = connection.getErrorStream();
			if(inputStream == null)
				inputStream = connection.getInputStream();
			Object responseBody = null;
			if(inputStream != null) {
				try {
					int responseContentLength = connection.getContentLength();
					Class<?> responseType = responseTypes.get(responseCode);
					responseBody = deserializeResponseBody(
							responseContentType, responseContentLength, inputStream, responseType);
				}
				finally {
					inputStream.close();
				}
			}
			return new ResponseEntity(responseCode, responseHeaders, responseBody);
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
	
	protected Object deserializeResponseBody(
			String contentType,
			int contentLength,
			InputStream inputStream, Class<?> responseType) throws IOException {
		BodyDeserializer deserializer = dataConverters.getBodyDeserializer(contentType);
		if(deserializer == null)
			throw new IOException("has no deserializer for: " + contentType);
		Object body = null;
		if(responseType != null) {
			if(responseType == String.class)
				body = deserializer.deserializeToString(inputStream, contentLength);
			else
				body = deserializer.deserialize(inputStream, responseType);
		}
		else {
			try {
				body = deserializer.deserializeToString(inputStream, contentLength);
			}
			catch (IOException e) {
				throw e;
			}
			if(body != null) {
				try {
					body = deserializer.deserialize((String)body, Map.class);
				}
				catch (Exception e) {
					// do nothing
				}
			}
		}
		return body;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getResponseBody(ResponseEntity entity) throws Exception {
		int statusCode = entity.getStatus();
		Object body = entity.getBody();
		if(statusCode < 400)
			return (T)body;
		if(statusCode == StatusCodes.BAD_REQUEST)
			throw new HttpBadRequestException(body);
		if(statusCode == StatusCodes.NOT_FOUND)
			throw new HttpNotFoundException(body);
		if(statusCode == StatusCodes.UNAUTHORIZED)
			throw new HttpUnauthorizedException(body);
		if(statusCode == StatusCodes.FORBIDDEN)
			throw new HttpForbiddenException(body);
		if(statusCode == StatusCodes.METHOD_NOT_ALLOWED)
			throw new HttpMethodNotAllowedException(body);
		if(statusCode == StatusCodes.NOT_ACCEPTABLE)
			throw new HttpNotAcceptableException(body);
		if(statusCode == StatusCodes.REQUEST_TIMEOUT)
			throw new HttpRequestTimeoutException(body);
		if(statusCode == StatusCodes.CONFLICT)
			throw new HttpConflictException(body);
		if(statusCode == StatusCodes.UNSUPPORTED_MEDIA_TYPE)
			throw new HttpUnsupportedMediaTypeException(body);
		if(statusCode == StatusCodes.INTERNAL_SERVER_ERROR)
			throw new HttpInternalServerErrorException(body);
		throw new HttpRequestException(statusCode, body);
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
