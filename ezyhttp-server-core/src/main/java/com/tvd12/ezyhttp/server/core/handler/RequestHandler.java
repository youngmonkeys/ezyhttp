package com.tvd12.ezyhttp.server.core.handler;

import java.lang.reflect.Method;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

public interface RequestHandler {
	
	EmptyRequestHandler EMPTY = EmptyRequestHandler.getInstance();

	void setController(Object controller);
	
	void setHandlerMethod(Method method);
	
	Object handle(RequestArguments arguments) throws Exception;
	
	Method getHandlerMethod();

	HttpMethod getMethod();
	
	String getRequestURI();
	
	String getResponseContentType();
	
}
