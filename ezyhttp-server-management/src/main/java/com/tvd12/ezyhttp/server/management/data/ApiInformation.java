package com.tvd12.ezyhttp.server.management.data;

import java.util.List;
import java.util.stream.Collectors;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestURI;

import lombok.Getter;

@Getter
public class ApiInformation {
    private final String uri;
    private final HttpMethod method;
    private final boolean management;
    private final List<JavaMethod> handlers;

    public ApiInformation(
        RequestURI requestUri,
        List<RequestHandler> handlerMethods
    ) {
        this.uri = requestUri.getUri();
        this.method = requestUri.getMethod();
        this.management = requestUri.isManagement();
        this.handlers = handlerMethods.stream()
            .map(it -> new JavaMethod(it.getHandlerMethod()))
            .collect(Collectors.toList());
    }
}
