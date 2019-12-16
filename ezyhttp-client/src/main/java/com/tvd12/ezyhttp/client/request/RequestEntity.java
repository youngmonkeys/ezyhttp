package com.tvd12.ezyhttp.client.request;

import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.Headers;

import lombok.Getter;

@Getter
public class RequestEntity {

	protected final Object body;
	protected final Map<String, String> headers;
	
	public RequestEntity(Map<String, String> headers, Object body) {
		this.body = body;
		this.headers = headers;
	}
	
	public static Builder body(Object body) {
		return builder().body(body);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getBody() {
		return (T)body;
	}
	
	public String getHeader(String name) {
		String value = headers.get(name);
		return value;
	}
	
	public String getContentType() {
		if(headers == null)
			return ContentTypes.APPLICATION_JSON;
		String contentType = headers.getOrDefault(Headers.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
		return contentType;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder implements EzyBuilder<RequestEntity> {

		protected Object body;
		protected Map<String, String> headers;
		
		public Builder body(Object body) {
			this.body = body;
			return this;
		}
		
		public Builder header(String name, String value) {
			if(this.headers == null)
				this.headers = new HashMap<>();
			this.headers.put(name, value);
			return this;
		}
		
		public Builder headers(Map<String, String> headers) {
			if(this.headers == null)
				this.headers = new HashMap<>();
			this.headers.putAll(headers);
			return this;
		}
		
		public Builder contentType(String contentType) {
			return header(Headers.CONTENT_TYPE, contentType);
		}
		
		@Override
		public RequestEntity build() {
			return new RequestEntity(headers, body);
		}
		
	}
	
}
