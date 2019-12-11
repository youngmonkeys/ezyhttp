package com.tvd12.ezyhttp.server.core.manager;

import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;

public class ExceptionHandlerManager {

	protected final Map<Class<?>, UncaughtExceptionHandler> uncaughtExceptionHandlers;
	
	public ExceptionHandlerManager() {
		this.uncaughtExceptionHandlers = new HashMap<>();
	}
	
	public void addUncaughtExceptionHandler(
			Class<?> exceptionClass, UncaughtExceptionHandler handler) {
		this.uncaughtExceptionHandlers.put(exceptionClass, handler);
	}
	
	public UncaughtExceptionHandler getUncaughtExceptionHandler(
			Class<?> exceptionClass) {
		UncaughtExceptionHandler hanlder = uncaughtExceptionHandlers.get(exceptionClass);
		return hanlder;
	}
	
}
