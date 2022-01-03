package com.tvd12.ezyhttp.server.core.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.core.constant.ContentTypes;

import lombok.Getter;
import lombok.Setter;

@Getter
public class View {

    @Setter
    private Locale locale;
	private final String template;
	private final String contentType;
	private final List<Cookie> cookies;
	private final Map<String, String> headers;
	private final Map<String, Object> variables;
	
	protected View(Builder builder) {
		this.template = builder.template;
		this.locale = builder.locale;
		this.variables = builder.variables;
		this.contentType = builder.contentType;
		this.cookies = builder.cookies;
		this.headers = builder.headers;
	}
	
	public static View of(String template) {
		return View.builder().template(template).build();
	}
	
	public boolean containsVariable(String name) {
	    return variables.containsKey(name);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getVariable(String name) {
		return (T)variables.get(name);
	}
	
	public void setVariable(String name, Object value) {
	    this.variables.put(name, value);
	}
	
	public void appendToVariable(String name, Object value) {
	    appendToVariable(variables, name, value);
	}
	
	@SuppressWarnings("unchecked")
    public static void appendToVariable(
	    Map<String, Object> variables,
	    String variableName,
	    Object value
    ) {
	    ((List<Object>)variables.computeIfAbsent(
	        variableName, 
	        k -> new ArrayList<>())
        ).add(value);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder implements EzyBuilder<View> {
		private String template;
		private List<Cookie> cookies;
		private Map<String, String> headers;
		private Locale locale = Locale.ENGLISH;
		private String contentType = ContentTypes.TEXT_HTML_UTF8;
		private Map<String, Object> variables = new HashMap<>();
		
		public Builder template(String template) {
			this.template = template;
			return this;
		}
		
		public Builder locale(Locale locale) {
			this.locale = locale;
			return this;
		}
		
		public Builder locale(String locale) {
			return locale(new Locale(locale));
		}
		
		public Builder contentType(String contentType) {
			this.contentType = contentType;
			return this;
		}
		
		public Builder addVariable(String name, Object value) {
			this.variables.put(name, value);
			return this;
		}
		
		public Builder addVariables(Map<String, Object> variables) {
			this.variables.putAll(variables);
			return this;
		}
		
		public Builder appendToVariable(String name, Object value) {
            View.appendToVariable(variables, name, value);
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
		
		public Builder addCookie(Cookie cookie) {
			if(cookies == null)
				this.cookies = new LinkedList<>();
			this.cookies.add(cookie);
			return this;
		}
		
		public Builder addCookie(String name, Object value) {
			return addCookie(new Cookie(name, value.toString()));
		}
		
		@Override
		public View build() {
			if(template == null)
				throw new NullPointerException("template can not be null");
			if(cookies == null)
				cookies = Collections.emptyList();
			if(headers == null)
				headers = Collections.emptyMap();
			return new View(this);
		}
	}
}
