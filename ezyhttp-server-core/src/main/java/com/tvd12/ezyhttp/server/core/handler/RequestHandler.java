package com.tvd12.ezyhttp.server.core.handler;

import com.tvd12.ezyfox.reflect.EzyMethods;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

public interface RequestHandler {

    EmptyRequestHandler EMPTY = EmptyRequestHandler.getInstance();

    default void setController(Object controller) {}

    default void setHandlerMethod(Method method) {}

    default void setAccept(Set<String> accept) {}

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

    default Set<String> getAccept() {
        return Collections.emptySet();
    }
}
