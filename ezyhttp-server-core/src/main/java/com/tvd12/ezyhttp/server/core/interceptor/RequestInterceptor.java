package com.tvd12.ezyhttp.server.core.interceptor;

import java.lang.reflect.Method;

import com.tvd12.ezyhttp.server.core.request.RequestArguments;

public interface RequestInterceptor {

    default boolean preHandle(
        RequestArguments arguments,
        Method handler
    ) throws Exception {
        return true;
    }

    default void postHandle(
        RequestArguments arguments,
        Method handler
    ) {}
}
