package com.tvd12.ezyhttp.server.core.handler;

public interface UncaughtExceptionHandler {

	Object handleException(Exception exception) throws Exception;
	
	String getResponseContentType();
}
