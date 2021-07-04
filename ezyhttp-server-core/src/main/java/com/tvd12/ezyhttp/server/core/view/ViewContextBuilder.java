package com.tvd12.ezyhttp.server.core.view;

import com.tvd12.ezyfox.builder.EzyBuilder;

public abstract class ViewContextBuilder implements EzyBuilder<ViewContext> {
	protected TemplateResolver templateResolver;
	
	public ViewContextBuilder templateResolver(TemplateResolver templateResolver) {
		this.templateResolver = templateResolver;
		return this;
	}
}
