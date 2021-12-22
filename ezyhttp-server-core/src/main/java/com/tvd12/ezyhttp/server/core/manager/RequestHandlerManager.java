package com.tvd12.ezyhttp.server.core.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.net.URITree;
import com.tvd12.ezyhttp.server.core.exception.DuplicateURIMappingHandlerException;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestURI;

import lombok.Getter;
import lombok.Setter;

public class RequestHandlerManager extends EzyLoggable implements EzyDestroyable {

    @Setter
    protected boolean allowOverrideURI;
	protected final URITree uriTree;
	protected final Set<String> handledURIs;
	@Getter
	protected final RequestURIManager requestURIManager;
	protected final Map<RequestURI, RequestHandler> handlers;
	protected final Map<RequestURI, List<RequestHandler>> handlerListByURI;
	
	public RequestHandlerManager() {
		this.uriTree = new URITree();
		this.handlers = new HashMap<>();
		this.handledURIs = new HashSet<>();
		this.handlerListByURI = new HashMap<>();
		this.requestURIManager = new RequestURIManager();
	}
	
	public String getMatchedURI(String requestURI) {
	    String matchedURI = null;
        if(handledURIs.contains(requestURI))
            matchedURI = requestURI;
        if(matchedURI == null)
            matchedURI = uriTree.getMatchedURI(requestURI);
        return matchedURI;
	}
	
	public RequestHandler getHandler(
	        HttpMethod method, String matchedURI, boolean isManagement) {
		RequestURI requestURI = new RequestURI(method, matchedURI, isManagement);
		RequestHandler handler = handlers.get(requestURI);
		return handler != null ? handler : RequestHandler.EMPTY;
	}
	
	public void addHandler(RequestURI uri, RequestHandler handler) {
		RequestHandler old = this.handlers.put(uri, handler);
		if (old != null && !allowOverrideURI) {
		    throw new DuplicateURIMappingHandlerException(uri, old, handler);
		}
		this.handledURIs.add(uri.getUri());
		this.uriTree.addURI(uri.getUri());
		this.logger.info("add mapping uri: {}", uri);
		this.handlerListByURI
            .computeIfAbsent(uri, k -> new ArrayList<>())
            .add(handler);
		this.requestURIManager.addHandledURI(uri.getUri());
		if (uri.isApi()) {
		    this.requestURIManager.addApiUri(uri.getUri());
		    this.requestURIManager.addApiUri(uri.getSameURI());
		}
		if (uri.isAuthenticated()) {
		    this.requestURIManager.addAuthenticatedUri(uri.getUri());
		    this.requestURIManager.addAuthenticatedUri(uri.getSameURI());
		}
	}
	
	public void addHandlers(Map<RequestURI, List<RequestHandler>> handlers) {
		for(RequestURI uri : handlers.keySet()) {
			for (RequestHandler handler : handlers.get(uri)) {
			    addHandler(uri, handler);
			}
		}
	}
	
	public Map<RequestURI, List<RequestHandler>> getHandlerListByURI() {
	    return Collections.unmodifiableMap(handlerListByURI);
	}
	
	@Override
	public void destroy() {
		this.handlers.clear();
		this.handledURIs.clear();
		this.handlerListByURI.clear();
		this.allowOverrideURI = false;
		this.requestURIManager.destroy();
	}
}
