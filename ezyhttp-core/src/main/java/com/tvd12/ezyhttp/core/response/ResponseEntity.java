package com.tvd12.ezyhttp.core.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.data.MultiValueMap;

import lombok.Getter;

@Getter
public class ResponseEntity<T> {

	protected final T body;
	protected final int status;
	protected final MultiValueMap headers;
	
	public ResponseEntity(int status, MultiValueMap headers, T body) {
		this.body = body;
		this.status = status;
		this.headers = headers;
	}
	
	public ResponseEntity(int status, Map<String, List<String>> headers, T body) {
		this(status, headers != null ? new MultiValueMap(headers) : null, body);
	}
	
	public static Builder status(int status) {
		return builder().status(status);
	}
	
	public static Builder of(int status, Object body) {
		return status(status).body(body);
	}

	public static <T> ResponseEntity<T> ok() {
		return new ResponseEntity<T>(StatusCodes.OK, (MultiValueMap)null, null);
	}
	
	public static <T> ResponseEntity<T> ok(T body) {
		return new ResponseEntity<T>(StatusCodes.OK, (MultiValueMap)null, body);
	}
	
	public static <T> ResponseEntity<T> badRequest() {
		return new ResponseEntity<T>(StatusCodes.BAD_REQUEST, (MultiValueMap)null, null);
	}
	
	public static <T> ResponseEntity<T> badRequest(T body) {
		return new ResponseEntity<T>(StatusCodes.BAD_REQUEST, (MultiValueMap)null, body);
	}
	
	public static <T> ResponseEntity<T> notFound() {
		return new ResponseEntity<T>(StatusCodes.NOT_FOUND, (MultiValueMap)null, null);
	}
	
	public static <T> ResponseEntity<T> notFound(T body) {
		return new ResponseEntity<T>(StatusCodes.NOT_FOUND, (MultiValueMap)null, body);
	}
	
	public String getHeader(String name) {
		if(headers == null)
			return null;
		String value = headers.getValue(name);
		return value;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append("ResponseEntity(")
				.append("status: ").append(status).append(", ")
				.append("headers: ").append(headers).append(", ")
				.append("body: ").append(body != null ? body.getClass().getSimpleName() : "null")
				.append(")")
				.toString();
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@SuppressWarnings("rawtypes")
	public static class Builder implements EzyBuilder<ResponseEntity> {

		protected int status;
		protected Object body;
		protected Map<String, List<String>> headers;
		
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
			List<String> values = headers.get(name);
			if(values == null) {
				values = new ArrayList<>();
				headers.put(name, values);
			}
			values.add(value);
			return this;
		}
		
		public Builder headers(Map<String, String> headers) {
			for(Entry<String, String> header : headers.entrySet())
				header(header.getKey(), header.getValue());
			return this;
		}
		
		@Override
		public ResponseEntity build() {
			return new ResponseEntity<>(status, headers, body);
		}
		
	}
	
}
