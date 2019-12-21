package com.tvd12.ezyhttp.server.core.exception;

import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestURI;

public class DuplicateURIMappingHandler extends IllegalStateException {
	private static final long serialVersionUID = 2586181034307827101L;

	public DuplicateURIMappingHandler(
			RequestURI uri, 
			RequestHandler old, RequestHandler now) {
		super("duplicate mapping uri: " + uri + " <> " + old.getHandlerMethod() + " => " + now.getHandlerMethod());
	}
	
}
