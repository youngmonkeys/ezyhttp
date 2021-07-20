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
	
	public static final ResponseAsync ASYNC = ResponseAsync.getInstance();
	
	public ResponseEntity(int status, MultiValueMap headers, T body) {
		this.body = body;
		this.status = status;
		this.headers = headers;
	}
	
	public ResponseEntity(int status, Map<String, List<String>> headers, T body) {
		this(status, headers != null ? new MultiValueMap(headers) : null, body);
	}
	
	public static <T> Builder<T> status(int status) {
		return new Builder<T>().status(status);
	}

	public static <T> ResponseEntity<T> create(int status, T body) {
		return new ResponseEntity<>(status, (MultiValueMap)null, body);
	}
	
	public static <T> ResponseEntity<T> badRequest() {
		return create(StatusCodes.BAD_REQUEST, null);
	}
	
	public static <T> ResponseEntity<T> badRequest(T body) {
		return create(StatusCodes.BAD_REQUEST, body);
	}
	
	public static <T> ResponseEntity<T> notFound() {
		return create(StatusCodes.NOT_FOUND, null);
	}
	
	public static <T> ResponseEntity<T> notFound(T body) {
		return create(StatusCodes.NOT_FOUND, body);
	}
	
	public T getBody() {
		return this.body;
	}
	
	public String getHeader(String name) {
		if(headers == null)
			return null;
		return headers.getValue(name);
	}
	
	@Override
	public String toString() {
		return "ResponseEntity(" +
				"status: " + status + ", " +
				"headers: " + headers + ", " +
				"body: " + (body != null ? body.getClass().getSimpleName() : "null") +
				")";
	}

	public static <T> Builder<T> builder() {
		return new Builder<>();
	}
	
	public static class Builder<T> implements EzyBuilder<ResponseEntity<T>> {

		protected int status;
		protected T body;
		protected Map<String, List<String>> headers;
		
		public Builder<T> status(int status) {
			this.status = status;
			return this;
		}
		
		public Builder<T> body(T body) {
			this.body = body;
			return this;
		}
		
		public Builder<T> header(String name, String value) {
			if(this.headers == null)
				this.headers = new HashMap<>();
			List<String> values = headers.computeIfAbsent(name, k -> new ArrayList<>());
			values.add(value);
			return this;
		}
		
		public Builder<T> headers(Map<String, String> headers) {
			for(Entry<String, String> header : headers.entrySet())
				header(header.getKey(), header.getValue());
			return this;
		}
		
		public Builder<T> header(String name, List<String> values) {
			for(String value : values)
				header(name, value);
			return this;
		}
		
		@Override
		public ResponseEntity<T> build() {
			return new ResponseEntity<>(status, headers, body);
		}
		
	}
	
}
