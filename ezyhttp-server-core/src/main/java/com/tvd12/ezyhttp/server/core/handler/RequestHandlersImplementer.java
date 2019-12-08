package com.tvd12.ezyhttp.server.core.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;

public class RequestHandlersImplementer extends EzyLoggable {
	
	public Map<String, RequestHandler> implement(Collection<Object> controllers) {
		Map<String, RequestHandler> handlers = new HashMap<>();
		for(Object controller : controllers)
			handlers.putAll(implement(controller));
		return handlers;
	}
	
	public Map<String, RequestHandler> implement(Object controller) {
		Map<String, RequestHandler> handlers = new HashMap<>();
		ControllerProxy proxy = new ControllerProxy(controller);
		for(RequestHandlerMethod method : proxy.getRequestHandlerMethods()) {
			RequestHandlerImplementer implementer = newImplementer(proxy, method);
			RequestHandler handler = implementer.implement();
			handlers.put(method.getRequestURI(), handler);
		}
		return handlers;
	}
	
	protected RequestHandlerImplementer newImplementer(
			ControllerProxy controller, RequestHandlerMethod method) {
		return new RequestHandlerImplementer(controller, method);
	}
	
}
