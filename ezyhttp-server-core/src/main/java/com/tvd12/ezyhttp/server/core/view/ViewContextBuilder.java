package com.tvd12.ezyhttp.server.core.view;

import java.util.ArrayList;
import java.util.List;

import com.tvd12.ezyfox.builder.EzyBuilder;

public abstract class ViewContextBuilder implements EzyBuilder<ViewContext> {
    
	protected TemplateResolver templateResolver;
	protected List<ViewDialect> viewDialects;
	protected List<ViewDecorator> viewDecorators;
	protected List<MessageProvider> messageProviders;
	protected AbsentMessageResolver absentMessageResolver;
	
	public ViewContextBuilder() {
	    this.viewDialects = new ArrayList<>();
	    this.viewDecorators = new ArrayList<>();
	    this.messageProviders = new ArrayList<>();
	}
	
	public ViewContextBuilder templateResolver(TemplateResolver templateResolver) {
		this.templateResolver = templateResolver;
		return this;
	}
	
	public ViewContextBuilder viewDialects(List<ViewDialect> viewDialects) {
	    this.viewDialects.addAll(viewDialects);
	    return this;
	}
	
	public ViewContextBuilder viewDecorators(List<ViewDecorator> viewDecorators) {
	    this.viewDecorators.addAll(viewDecorators);
	    return this;
	}
	
	public ViewContextBuilder messageProviders(List<MessageProvider> messageProviders) {
	    this.messageProviders.addAll(messageProviders);
	    return this;
	}
	
	public ViewContextBuilder absentMessageResolver(AbsentMessageResolver absentMessageResolver) {
	    if (absentMessageResolver != null) {
	        this.absentMessageResolver = absentMessageResolver;
	    }
	    return this;
	}
}
