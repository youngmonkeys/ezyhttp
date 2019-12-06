package com.tvd12.ezyhttp.server.core.handler;

import com.tvd12.ezyhttp.server.core.request.RequestArguments;

public interface RequestHandler {

	void setController(Object controller);
	
	Object handle(RequestArguments arguments) throws Exception;
	
	String getResponseContentType();
}
