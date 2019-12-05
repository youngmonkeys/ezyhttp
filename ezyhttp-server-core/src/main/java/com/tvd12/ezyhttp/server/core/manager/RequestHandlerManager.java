package com.tvd12.ezyhttp.server.core.manager;

import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyhttp.server.core.handler.RequestHandler;

public class RequestHandlerManager {

	protected final Map<String, RequestHandler> handlers;
	
	public RequestHandlerManager() {
		this.handlers = new HashMap<>();
	}
	
	public void addHandler(String uri, RequestHandler handler) {
		this.handlers.put(uri, handler);
	}
	
	public RequestHandler getHandler(String uri) {
		RequestHandler handler = handlers.get(uri);
		return handler;
	}
	
}
