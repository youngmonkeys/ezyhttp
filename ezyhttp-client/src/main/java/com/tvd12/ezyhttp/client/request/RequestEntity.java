package com.tvd12.ezyhttp.client.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.core.data.MultiValueMap;

import lombok.Getter;

@Getter
public class RequestEntity<T> {

	protected final T body;
	protected final MultiValueMap headers;

	public RequestEntity() {
		this.body = null;
		this.headers = null;
	}

	public RequestEntity(T body) {
		this.body = body;
		this.headers = null;
	}
	public RequestEntity(MultiValueMap headers, T body) {
		this.body = body;
		this.headers = headers;
	}

	public static <T> Builder<T> of(T body) {
		return new Builder<T>().body(body);
	}

	public T getBody() {
		return this.body;
	}

	@Override
	public String toString() {
		return "RequestEntity(" +
				"headers: " + headers + ", " +
				"body: " + (body != null ? body.getClass().getSimpleName() : "null") +
				")";
	}
	
	public static class Builder<T> implements EzyBuilder<RequestEntity<T>> {

		protected T body;
		protected Map<String, List<String>> headers;
		
		public Builder<T> body(T body) {
			if(body != null) {
				this.body = body;
			}
			return this;
		}
		
		public Builder<T> header(String name, String value) {
			if(this.headers == null)
				this.headers = new HashMap<>();
			List<String> values = headers.computeIfAbsent(name, k -> new ArrayList<>());
			values.add(value);
			return this;
		}

		@Override
		public RequestEntity<T> build() {
			return new RequestEntity<>(new MultiValueMap(this.headers), this.body);
		}
		
	}
	
}
