package com.tvd12.ezyhttp.server.core.handler;

import java.lang.reflect.Method;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

public class EmptyRequestHandler implements RequestHandler {
	
	private static final EmptyRequestHandler INSTANCE = new EmptyRequestHandler();
	
	private EmptyRequestHandler() {}
	
	public static EmptyRequestHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public void setController(Object controller) {}

	@Override
	public void setHandlerMethod(Method method) {}

	@Override
	public Object handle(RequestArguments arguments) throws Exception {
		return null;
	}

	@Override
	public Method getHandlerMethod() {
		return null;
	}

	@Override
	public HttpMethod getMethod() {
		return null;
	}

	@Override
	public String getRequestURI() {
		return null;
	}

	@Override
	public String getResponseContentType() {
		return null;
	}
	
}
