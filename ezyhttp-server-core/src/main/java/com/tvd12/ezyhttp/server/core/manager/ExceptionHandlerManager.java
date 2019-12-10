package com.tvd12.ezyhttp.server.core.manager;

import java.util.ArrayList;
import java.util.List;

import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;

public class ExceptionHandlerManager {

	protected final List<UncaughtExceptionHandler> uncaughtExceptionHandlers;
	
	public ExceptionHandlerManager() {
		this.uncaughtExceptionHandlers = new ArrayList<>();
	}
	
	public void addUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
		this.uncaughtExceptionHandlers.add(handler);
	}
	
	public List<UncaughtExceptionHandler> getUncaughtExceptionHandlers() {
		return uncaughtExceptionHandlers;
	}
	
}
