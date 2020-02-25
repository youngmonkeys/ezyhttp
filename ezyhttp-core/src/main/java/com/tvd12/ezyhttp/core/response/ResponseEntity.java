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
public class ResponseEntity {

	protected final Object body;
	protected final int status;
	protected final MultiValueMap headers;
	
	public static final ResponseNothing NOTHING = ResponseNothing.getInstance();
	
	public ResponseEntity(int status, MultiValueMap headers, Object body) {
		this.body = body;
		this.status = status;
		this.headers = headers;
	}
	
	public ResponseEntity(int status, Map<String, List<String>> headers, Object body) {
		this(status, headers != null ? new MultiValueMap(headers) : null, body);
	}
	
	public static Builder status(int status) {
		return builder().status(status);
	}
	
	public static Builder of(int status, Object body) {
		return status(status).body(body);
	}
	
	public static ResponseEntity create(int status, Object body) {
		return new ResponseEntity(status, (MultiValueMap)null, body);
	}

	public static ResponseEntity ok() {
		return create(StatusCodes.OK, null);
	}
	
	public static ResponseEntity ok(Object body) {
		return create(StatusCodes.OK, body);
	}
	
	public static ResponseEntity badRequest() {
		return create(StatusCodes.BAD_REQUEST, null);
	}
	
	public static ResponseEntity badRequest(Object body) {
		return create(StatusCodes.BAD_REQUEST, body);
	}
	
	public static ResponseEntity notFound() {
		return create(StatusCodes.NOT_FOUND, null);
	}
	
	public static ResponseEntity notFound(Object body) {
		return create(StatusCodes.NOT_FOUND, body);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getBody() {
		return (T)body;
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
		
		public Builder header(String name, List<String> values) {
			for(String value : values)
				header(name, value);
			return this;
		}
		
		@Override
		public ResponseEntity build() {
			return new ResponseEntity(status, headers, body);
		}
		
	}
	
}
