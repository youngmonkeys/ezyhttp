package com.tvd12.ezyhttp.server.core.asm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;
import com.tvd12.ezyhttp.server.core.request.RequestURI;

public class RequestHandlersImplementer extends EzyLoggable {
	
	public Map<RequestURI, RequestHandler> implement(Collection<Object> controllers) {
		Map<RequestURI, RequestHandler> handlers = new HashMap<>();
		for(Object controller : controllers)
			handlers.putAll(implement(controller));
		return handlers;
	}
	
	public Map<RequestURI, RequestHandler> implement(Object controller) {
		Map<RequestURI, RequestHandler> handlers = new HashMap<>();
		ControllerProxy proxy = new ControllerProxy(controller);
		for(RequestHandlerMethod method : proxy.getRequestHandlerMethods()) {
			RequestHandlerImplementer implementer = newImplementer(proxy, method);
			RequestHandler handler = implementer.implement();
			HttpMethod httpMethod = handler.getMethod();
			RequestURI uri = new RequestURI(httpMethod, method.getRequestURI());
			handlers.put(uri, handler);
		}
		return handlers;
	}
	
	protected RequestHandlerImplementer newImplementer(
			ControllerProxy controller, RequestHandlerMethod method) {
		return new RequestHandlerImplementer(controller, method);
	}
	
}
