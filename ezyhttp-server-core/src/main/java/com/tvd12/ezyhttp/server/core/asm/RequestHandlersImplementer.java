package com.tvd12.ezyhttp.server.core.asm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;
import com.tvd12.ezyhttp.server.core.request.RequestURI;

public class RequestHandlersImplementer extends EzyLoggable {
	
	public Map<RequestURI, List<RequestHandler>> implement(
	        Collection<Object> controllers
    ) {
		Map<RequestURI, List<RequestHandler>> handlers = new HashMap<>();
		for(Object controller : controllers) {
			Map<RequestURI, List<RequestHandler>> map = implement(controller);
			for(RequestURI uri : map.keySet()) {
			    handlers.computeIfAbsent(uri, k -> new ArrayList<>())
			            .addAll(map.get(uri));
			}
		}
		return handlers;
	}
	
	public Map<RequestURI, List<RequestHandler>> implement(Object controller) {
		Map<RequestURI, List<RequestHandler>> handlers = new HashMap<>();
		ControllerProxy proxy = new ControllerProxy(controller);
		boolean isManagement = proxy.isManagement();
		for(RequestHandlerMethod method : proxy.getRequestHandlerMethods()) {
			RequestHandlerImplementer implementer = newImplementer(proxy, method);
			RequestHandler handler = implementer.implement();
			HttpMethod httpMethod = handler.getMethod();
			String requestURI = method.getRequestURI(); 
			RequestURI uri = new RequestURI(httpMethod, requestURI, isManagement);
			handlers.computeIfAbsent(uri, k -> new ArrayList<>())
			        .add(handler);
		}
		return handlers;
	}
	
	protected RequestHandlerImplementer newImplementer(
			ControllerProxy controller, RequestHandlerMethod method) {
		return new RequestHandlerImplementer(controller, method);
	}
	
}
