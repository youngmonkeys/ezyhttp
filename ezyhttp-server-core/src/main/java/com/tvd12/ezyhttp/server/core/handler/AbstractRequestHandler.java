package com.tvd12.ezyhttp.server.core.handler;

import java.io.InputStream;

import com.tvd12.ezyhttp.server.core.request.RequestArguments;

@SuppressWarnings("unchecked")
public abstract class AbstractRequestHandler implements RequestHandler {

	@Override
	public Object handle(RequestArguments arguments) throws Exception {
		try {
			return handleRequest(arguments);
		}
		catch (Exception e) {
			return handleException(e);
		}
	}
	
	protected abstract Object handleRequest(RequestArguments arguments) throws Exception;
	
	protected abstract Object handleException(Exception e) throws Exception;
	
	protected <T> T deserializeHeader(String header, Class<T> type) {
		return (T)header;
	}
	
	protected <T> T deserializeParameter(String parameter, Class<T> type) {
		return (T)parameter;
	}
	
	protected <T> T deserializeBody(InputStream inputStream, Class<T> type) {
		return null;
	}
	
}
