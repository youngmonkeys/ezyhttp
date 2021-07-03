package com.tvd12.ezyhttp.server.core.view;

import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.VIEW_TEMPLATE_CACHEABLE;
import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.VIEW_TEMPLATE_CACHE_TTL_MS;
import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.VIEW_TEMPLATE_MESSAGES_LOCATION;
import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.VIEW_TEMPLATE_MODE;
import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.VIEW_TEMPLATE_PREFIX;
import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.VIEW_TEMPLATE_SUFFIX;

import com.tvd12.ezyfox.bean.EzyPropertyFetcher;
import com.tvd12.ezyfox.builder.EzyBuilder;

import lombok.Getter;

@Getter
public class TemplateResolver {

	private final String prefix;
	private final String suffix;
	private final int cacheTTLMs;
	private final boolean cacheable;
	private final String templateMode;
	private final String messagesLocation;
	
	protected TemplateResolver(Builder builder) {
		this.prefix = builder.prefix;
		this.suffix = builder.suffix;
		this.cacheTTLMs = builder.cacheTTLMs;
		this.cacheable = builder.cacheable;
		this.templateMode = builder.templateMode;
		this.messagesLocation = builder.messagesLocation;
	}
	
	public static TemplateResolver of(EzyPropertyFetcher propertyFetcher) {
		return builder().setFrom(propertyFetcher).build();
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	
    public static class Builder implements EzyBuilder<TemplateResolver> {
    	private String prefix = "templates/";
    	private String suffix = ".html";
    	private int cacheTTLMs = 3600000;
    	private boolean cacheable = true;
    	private String templateMode = "HTML";
    	private String messagesLocation = "messages";
    	
    	public Builder prefix(String prefix) {
    		this.prefix = prefix;
    		return this;
    	}
    	
    	public Builder suffix(String suffix) {
    		this.suffix = suffix;
    		return this;
    	}
    	
    	public Builder cacheTTLMs(int cacheTTLMs) {
    		this.cacheTTLMs = cacheTTLMs;
    		return this;
    	}
    	
    	public Builder cacheable(boolean cacheable) {
    		this.cacheable = cacheable;
    		return this;
    	}
    	
    	public Builder templateMode(String templateMode) {
    		this.templateMode = templateMode;
    		return this;
    	}
    	
    	public Builder messagesLocation(String messagesLocation) {
    		this.messagesLocation = messagesLocation;
    		return this;
    	}
    	
    	public Builder setFrom(EzyPropertyFetcher propertyFetcher) {
    		this.templateMode = propertyFetcher.getProperty(
    				VIEW_TEMPLATE_MODE, String.class, templateMode);
    		this.prefix = propertyFetcher.getProperty(
    				VIEW_TEMPLATE_PREFIX, String.class, prefix);
    		this.suffix = propertyFetcher.getProperty(
    				VIEW_TEMPLATE_SUFFIX, String.class, suffix);
    		this.cacheTTLMs = propertyFetcher.getProperty(
    				VIEW_TEMPLATE_CACHE_TTL_MS, int.class, cacheTTLMs);
    		this.cacheable = propertyFetcher.getProperty(
    				VIEW_TEMPLATE_CACHEABLE, boolean.class, cacheable);
    		this.messagesLocation = propertyFetcher.getProperty(
    				VIEW_TEMPLATE_MESSAGES_LOCATION, String.class, messagesLocation);
    		return this;
    	}
    	
    	@Override
    	public TemplateResolver build() {
    		return new TemplateResolver(this);
    	}
    }
	
}
