package com.tvd12.ezyhttp.server.core.asm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerMethod;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerProxy;

public class ExceptionHandlersImplementer extends EzyLoggable {

    public Map<Class<?>, UncaughtExceptionHandler>
            implement(Collection<Object> exceptionHandlers) {
        Map<Class<?>, UncaughtExceptionHandler> handlers = new HashMap<>();
        for (Object controller : exceptionHandlers)
            handlers.putAll(implement(controller));
        return handlers;
    }

    public Map<Class<?>, UncaughtExceptionHandler> implement(Object exceptionHandler) {
        Map<Class<?>, UncaughtExceptionHandler> handlers = new HashMap<>();
        ExceptionHandlerProxy proxy = new ExceptionHandlerProxy(exceptionHandler);
        for (ExceptionHandlerMethod method : proxy.getExceptionHandlerMethods()) {
            ExceptionHandlerImplementer implementer = newImplementer(proxy, method);
            UncaughtExceptionHandler handler = implementer.implement();
            for (Class<?> exceptionClass : method.getExceptionClasses())
                handlers.put(exceptionClass, handler);
        }
        return handlers;
    }

    protected ExceptionHandlerImplementer newImplementer(
            ExceptionHandlerProxy exceptionHandler, ExceptionHandlerMethod method) {
        return new ExceptionHandlerImplementer(exceptionHandler, method);
    }

}
