package com.tvd12.ezyhttp.server.core.view;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.core.constant.ContentTypes;

import lombok.Getter;

@Getter
public class View {

	private final String template;
	private final Locale locale;
	private final String contentType;
	private final Map<String, Object> variables;
	
	protected View(Builder builder) {
		this.template = builder.template;
		this.locale = builder.locale;
		this.variables = builder.variables;
		this.contentType = builder.contentType;
	}
	
	public static View of(String template) {
		return View.builder().template(template).build();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getVariable(String name) {
		return (T)variables.get(name);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder implements EzyBuilder<View> {
		private String template;
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
		
		@Override
		public View build() {
			if(template == null)
				throw new NullPointerException("template can not be null");
			return new View(this);
		}
	}
}
