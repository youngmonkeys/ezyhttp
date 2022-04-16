package com.tvd12.ezyhttp.core.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.Headers;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.data.MultiValueMap;

import lombok.Getter;

@Getter
public class ResponseEntity {

	protected final Object body;
	protected final int status;
	protected final MultiValueMap headers;
	
	public static final ResponseAsync ASYNC = ResponseAsync.getInstance();
	
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
	
	public static ResponseEntity noContent() {
        return create(StatusCodes.NO_CONTENT, null);
    }
	
	@SuppressWarnings("unchecked")
	public <T> T getBody() {
		return (T)body;
	}
	
	public String getHeader(String name) {
		if(headers == null)
			return null;
		return headers.getValue(name);
	}
	
	public String getContentType() {
		return getHeader(Headers.CONTENT_TYPE);
	}
	
	@Override
	public String toString() {
		return "ResponseEntity(" +
			"status: " + status + ", " +
			"headers: " + headers + ", " +
			"body: " + (body != null ? body.getClass().getSimpleName() : "null") +
			")";
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder implements EzyBuilder<ResponseEntity> {

		protected Object body;
		protected int status = StatusCodes.OK;
		protected Map<String, List<String>> headers;
		
		public Builder status(int status) {
			this.status = status;
			return this;
		}
		
		public Builder body(Object body) {
			this.body = body;
			return this;
		}
		
		public Builder textPlain(Object value) {
			this.textPlain();
			return body(value.toString());
		}
		
		public Builder textPlain() {
			return contentType(ContentTypes.TEXT_PLAIN);
		}
		
		public Builder contentType(String contentType) {
			return header(Headers.CONTENT_TYPE, contentType);
		}
		
		public Builder header(String name, String value) {
			if(this.headers == null) {
				this.headers = new HashMap<>();
			}
			headers
				.computeIfAbsent(name, k -> new ArrayList<>())
				.add(value);
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
