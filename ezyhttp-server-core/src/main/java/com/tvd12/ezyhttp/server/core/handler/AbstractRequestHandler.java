package com.tvd12.ezyhttp.server.core.handler;

import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

import com.tvd12.ezyhttp.server.core.codec.BodyDeserializer;
import com.tvd12.ezyhttp.server.core.codec.BodySerializer;
import com.tvd12.ezyhttp.server.core.codec.DataConverters;
import com.tvd12.ezyhttp.server.core.codec.StringDeserializer;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

public abstract class AbstractRequestHandler implements RequestHandler {

	protected final DataConverters dataConverters;
	protected final BodySerializer bodySerializer;
	protected final ComponentManager componentManager;
	
	public AbstractRequestHandler() {
		this.componentManager = ComponentManager.getInstance();
		this.dataConverters = componentManager.getDataConverters();
		this.bodySerializer = dataConverters.getBodySerializer();
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
		String contentType = request.getContentType();
		BodyDeserializer deserializer = dataConverters.getBodyDeserializer(contentType);
		if(deserializer == null)
			throw new IOException("has no body deserializer for: " + type.getName());
		ServletInputStream inputStream = request.getInputStream();
		T body = deserializer.deserialize(inputStream, type);
		return body;
	}
	
}
