package com.tvd12.ezyhttp.server.core.handler;

public interface UncaughtExceptionHandler {

	void setExceptionHandler(Object exceptionHandler);
	
	Object handleException(Exception exception) throws Exception;
	
	String getResponseContentType();
}
