package com.tvd12.ezyhttp.server.core.util;

import java.util.Comparator;

import com.tvd12.ezyhttp.core.annotation.Interceptor;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;

public final class InterceptorAnnotations {

    private InterceptorAnnotations() {
    }

    public static int getPriority(Interceptor annotation) {
        return annotation.priority();
    }

    public static int getPriority(RequestInterceptor interceptor) {
        Interceptor annotation = interceptor.getClass().getAnnotation(Interceptor.class);
        return getPriority(annotation);
    }

    public static Comparator<Object> comparator() {
        return (a, b) ->
                getPriority(a.getClass().getAnnotation(Interceptor.class))
                        - getPriority(b.getClass().getAnnotation(Interceptor.class));
    }
}
