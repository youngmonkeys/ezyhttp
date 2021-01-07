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
	
	public RequestHandler getHandler(HttpMethod method, String uri) {
		String matchedURI = null;
		if(handledURIs.contains(uri))
			matchedURI = uri;
		if(matchedURI == null)
			matchedURI = uriTree.getMatchedURI(uri);
		if(matchedURI == null)
			return null;
		RequestHandler handler = handlers.get(new RequestURI(method, matchedURI));
		return handler != null ? handler : RequestHandler.EMPTY;
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
