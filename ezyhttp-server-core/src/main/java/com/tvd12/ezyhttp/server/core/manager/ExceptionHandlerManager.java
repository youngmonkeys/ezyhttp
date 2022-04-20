package com.tvd12.ezyhttp.server.core.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;

public class ExceptionHandlerManager extends EzyLoggable implements EzyDestroyable {

    protected final List<Object> exceptionHandlers;
    protected final Map<Class<?>, UncaughtExceptionHandler> uncaughtExceptionHandlers;

    public ExceptionHandlerManager() {
        this.exceptionHandlers = new ArrayList<>();
        this.uncaughtExceptionHandlers = new HashMap<>();
    }

    public void addExceptionHandler(Object exceptionHandler) {
        this.exceptionHandlers.add(exceptionHandler);
    }

    public void addExceptionHandlers(List<Object> exceptionHandlers) {
        this.exceptionHandlers.addAll(exceptionHandlers);
    }

    public List<Object> getExceptionHandlerList() {
        return new ArrayList<>(exceptionHandlers);
    }

    public UncaughtExceptionHandler getUncaughtExceptionHandler(
            Class<?> exceptionClass) {
        return uncaughtExceptionHandlers.get(exceptionClass);
    }

    public void addUncaughtExceptionHandler(
            Class<?> exceptionClass, UncaughtExceptionHandler handler) {
        this.uncaughtExceptionHandlers.putIfAbsent(exceptionClass, handler);
        this.logger.info("add exception handler for: " + exceptionClass.getName());
    }

    public void addUncaughtExceptionHandlers(
            Map<Class<?>, UncaughtExceptionHandler> handlers) {
        for (Class<?> exceptionClass : handlers.keySet()) {
            addUncaughtExceptionHandler(exceptionClass, handlers.get(exceptionClass));
        }
    }

    public Map<Class<?>, UncaughtExceptionHandler> getUncaughtExceptionHandlers() {
        return new HashMap<>(uncaughtExceptionHandlers);
    }

    @Override
    public void destroy() {
        this.exceptionHandlers.clear();
        this.uncaughtExceptionHandlers.clear();
    }
}
