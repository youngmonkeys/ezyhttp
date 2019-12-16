package com.tvd12.ezyhttp.core.response;

import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.core.constant.StatusCodes;

import lombok.Getter;

@Getter
public class ResponseEntity<T> {

	protected final T body;
	protected final int status;
	protected final Map<String, String> headers;
	
	public ResponseEntity() {
		this(StatusCodes.OK);
	}
	
	public ResponseEntity(int status) {
		this(status, null);
	}
	
	public ResponseEntity(int status, Map<String, String> headers) {
		this(status, headers, null);
	}
	
	public ResponseEntity(int status, Map<String, String> headers, T body) {
		this.status = status;
		this.headers = headers;
		this.body = body;
	}
	
	public static Builder status(int status) {
		return builder().status(status);
	}
	
	public static Builder of(int status, Object body) {
		return status(status).body(body);
	}
	
	public String getHeader(String name) {
		String value = headers.get(name);
		return value;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@SuppressWarnings("rawtypes")
	public static class Builder implements EzyBuilder<ResponseEntity> {

		protected int status;
		protected Object body;
		protected Map<String, String> headers;
		
		public Builder status(int status) {
			this.status = status;
			return this;
		}
		
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
		
		@Override
		public ResponseEntity build() {
			return new ResponseEntity<>(status, headers, body);
		}
		
	}
	
}
