package com.tvd12.ezyhttp.server.core.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.net.URITree;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestURI;

public class RequestHandlerManager extends EzyLoggable {

	protected final URITree uriTree;
	protected final Set<String> handledURIs;
	protected final Map<RequestURI, RequestHandler> handlers;
	
	public RequestHandlerManager() {
		this.uriTree = new URITree();
		this.handlers = new HashMap<>();
		this.handledURIs = new HashSet<>();
	}
	
	public boolean hasHandler(String uri) {
		boolean answer = handledURIs.contains(uri);
		if(!answer) {
			answer = uriTree.containsURI(uri);
		}
		return answer;
	}
	
	public RequestHandler getHandler(RequestURI uri) {
		RequestHandler handler = handlers.get(uri);
		if(handler == null) {
			String matchedURI = uriTree.getMatchedURI(uri.getUri());
			handler = handlers.get(new RequestURI(uri.getMethod(), matchedURI));
		}
		return handler;
	}
	
	public RequestHandler getHandler(HttpMethod method, String uri) {
		return getHandler(new RequestURI(method, uri));
	}
	
	public void addHandler(RequestURI uri, RequestHandler handler) {
		this.handlers.put(uri, handler);
		this.handledURIs.add(uri.getUri());
		this.uriTree.addURI(uri.getUri());
		this.logger.info("add mapping uri: {}", uri);
	}
	
	public void addHandlers(Map<RequestURI, RequestHandler> handlers) {
		for(RequestURI uri : handlers.keySet())
			addHandler(uri, handlers.get(uri));
	}
	
}
