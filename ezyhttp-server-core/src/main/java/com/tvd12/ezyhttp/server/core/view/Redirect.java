package com.tvd12.ezyhttp.server.core.view;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.util.EzyPair;

import lombok.Getter;

public class Redirect {

	@Getter
	private final String uri;
	@Getter
	private final List<Cookie> cookies;
	@Getter
	private final Map<String, String> headers;
	private final List<EzyPair<String, Object>> parameters;
	
	protected Redirect(Builder builder) {
		this.uri = builder.uri;
		this.cookies = builder.cookies;
		this.headers = builder.headers;
		this.parameters = builder.parameters;
	}
	
	public static Redirect to(String uri) {
		return Redirect.builder()
				.uri(uri)
				.build();
	}
	
	public String getQueryString() throws IOException {
		if(parameters.isEmpty())
			return "";
		char concatChar = '?';
		StringBuilder builder = new StringBuilder();
		for(EzyPair<String, Object> pair : parameters) {
			builder
				.append(concatChar)
				.append(pair.getKey())
				.append("=")
				.append(URLEncoder.encode(pair.getValue().toString(), EzyStrings.UTF_8));
			concatChar = '&';
		}
		return builder.toString();
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder implements EzyBuilder<Redirect> {
		
		private String uri;
		private List<Cookie> cookies;
		private Map<String, String> headers;
		private List<EzyPair<String, Object>> parameters;
		
		public Builder uri(String uri) {
			this.uri = uri;
			return this;
		}
		
		public Builder addHeader(String name, Object value) {
			if(headers == null)
				this.headers = new HashMap<>();
			this.headers.put(name, value.toString());
			return this;
		}
		
		public Builder addHeaders(Map<String, Object> headers) {
			for(Entry<String, Object> e : headers.entrySet())
				addHeader(e.getKey(), e.getValue());
			return this;
		}
		
		public Builder addParameters(Map<String, Object> parameters) {
			for(Entry<String, Object> e : parameters.entrySet())
				addParameter(e.getKey(), e.getValue());
			return this;
		}
		
		public Builder addParameter(String name, Object value) {
			if(parameters == null)
				parameters = new LinkedList<>();
			this.parameters.add(new EzyPair<>(name, value));
			return this;
		}
		
		public Builder addCookie(Cookie cookie) {
			if(cookies == null)
				this.cookies = new LinkedList<>();
			this.cookies.add(cookie);
			return this;
		}
		
		public Builder addCookie(String name, Object value) {
			if(EzyStrings.isNoContent(name))
				throw new IllegalArgumentException("cookie name can not be null or empty");
			if(value == null)
				throw new IllegalArgumentException("cookie value can not be null");
			return addCookie(new Cookie(name, value.toString()));
		}
		
		@Override
		public Redirect build() {
			if(cookies == null)
				cookies = Collections.emptyList();
			if(headers == null)
				headers = Collections.emptyMap();
			if(parameters == null)
				parameters = Collections.emptyList();
			return new Redirect(this);
		}
	}
}
