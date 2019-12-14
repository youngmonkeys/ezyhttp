package com.tvd12.ezyhttp.server.core.handler;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

import com.tvd12.ezyhttp.core.codec.BodyDeserializer;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.core.codec.StringDeserializer;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractRequestHandler implements RequestHandler {

	@Setter
	@Getter
	protected Method handlerMethod;
	protected final DataConverters dataConverters;
	protected final ComponentManager componentManager;
	
	public AbstractRequestHandler() {
		this.componentManager = ComponentManager.getInstance();
		this.dataConverters = componentManager.getDataConverters();
	}
	
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
	
	protected <T> T deserializeHeader(String header, Class<T> type) throws IOException {
		StringDeserializer deserializer = dataConverters.getStringDeserializer();
		T answer = deserializer.deserialize(header, type);
		return answer;
	}
	
	protected <T> T deserializeParameter(String parameter, Class<T> type) throws IOException {
		StringDeserializer deserializer = dataConverters.getStringDeserializer();
		T answer = deserializer.deserialize(parameter, type);
		return answer;
	}
	
	protected <T> T deserializeBody(ServletRequest request, Class<T> type) throws IOException {
		int contentLength = request.getContentLength();
		if(contentLength <= 0)
			return (T)null;
		String contentType = request.getContentType();
		BodyDeserializer deserializer = dataConverters.getBodyDeserializer(contentType);
		if(deserializer == null)
			throw new IOException("has no body deserializer for: " + contentType);
		ServletInputStream inputStream = request.getInputStream();
		T body = deserializer.deserialize(inputStream, type);
		return body;
	}
	
}
