package com.tvd12.ezyhttp.server.core.handler;

import java.lang.reflect.Method;

import com.tvd12.ezyfox.reflect.EzyMethods;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

@SuppressWarnings("AbbreviationAsWordInName")
public interface RequestHandler {

    EmptyRequestHandler EMPTY = EmptyRequestHandler.getInstance();

    default void setController(Object controller) {
    }

    default void setHandlerMethod(Method method) {
    }

    Object handle(RequestArguments arguments) throws Exception;

    default Method getHandlerMethod() {
        return EzyMethods.getMethod(getClass(), "handle", RequestArguments.class);
    }

    default boolean isAsync() {
        return false;
    }

    HttpMethod getMethod();

    String getRequestURI();

    String getResponseContentType();

}
