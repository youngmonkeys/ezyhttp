package com.tvd12.ezyhttp.server.core.manager;

import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyhttp.server.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestURI;

public class RequestHandlerManager {

	protected final Map<RequestURI, RequestHandler> handlers;
	
	public RequestHandlerManager() {
		this.handlers = new HashMap<>();
	}
	
	public RequestHandler getHandler(RequestURI uri) {
		RequestHandler handler = handlers.get(uri);
		return handler;
	}
	
	public RequestHandler getHandler(HttpMethod method, String uri) {
		return getHandler(new RequestURI(method, uri));
	}
	
	public void addHandler(RequestURI uri, RequestHandler handler) {
		this.handlers.put(uri, handler);
	}
	
	public void addHandlers(Map<RequestURI, RequestHandler> handlers) {
		this.handlers.putAll(handlers);
	}
	
}
