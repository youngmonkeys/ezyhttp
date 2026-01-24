package com.tvd12.ezyhttp.server.core.view;

import com.tvd12.ezyfox.builder.EzyBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class ViewContextBuilder implements EzyBuilder<ViewContext> {

    protected TemplateResolver templateResolver;
    protected AbsentMessageResolver absentMessageResolver;
    protected final List<ViewDialect> viewDialects;
    protected final List<ViewDecorator> viewDecorators;
    protected final List<MessageProvider> messageProviders;
    protected final List<ViewTemplateInputStreamLoader> templateInputStreamLoaders;

    public ViewContextBuilder() {
        this.viewDialects = new ArrayList<>();
        this.viewDecorators = new ArrayList<>();
        this.messageProviders = new ArrayList<>();
        this.templateInputStreamLoaders = new ArrayList<>();
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
        this.viewDecorators.sort(Comparator.comparingInt(ViewDecorator::getPriority));
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

    public ViewContextBuilder templateInputStreamLoaders(
        List<ViewTemplateInputStreamLoader> templateInputStreamLoaders
    ) {
        this.templateInputStreamLoaders.addAll(templateInputStreamLoaders);
        return this;
    }
}
