package com.tvd12.ezyhttp.server.core.asm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.exception.DuplicateURIMappingHandlerException;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;
import com.tvd12.ezyhttp.server.core.request.RequestURI;

public class RequestHandlersImplementer extends EzyLoggable {
	
	public Map<RequestURI, RequestHandler> implement(Collection<Object> controllers) {
		Map<RequestURI, RequestHandler> handlers = new HashMap<>();
		for(Object controller : controllers) {
			Map<RequestURI, RequestHandler> map = implement(controller);
			for(RequestURI uri : map.keySet()) {
				RequestHandler handler = map.get(uri);
				RequestHandler old = handlers.put(uri, handler);
				if(old != null)
					throw new DuplicateURIMappingHandlerException(uri, old, handler);
			}
		}
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
			RequestHandler old = handlers.put(uri, handler);
			if(old != null)
				throw new DuplicateURIMappingHandlerException(uri, old, handler);
		}
		return handlers;
	}
	
	protected RequestHandlerImplementer newImplementer(
			ControllerProxy controller, RequestHandlerMethod method) {
		return new RequestHandlerImplementer(controller, method);
	}
	
}
