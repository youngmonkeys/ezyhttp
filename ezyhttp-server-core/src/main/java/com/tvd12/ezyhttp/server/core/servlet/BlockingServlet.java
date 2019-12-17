package com.tvd12.ezyhttp.server.core.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.core.codec.BodySerializer;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.data.MultiValueMap;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.manager.ExceptionHandlerManager;
import com.tvd12.ezyhttp.server.core.manager.InterceptorManager;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.request.SimpleRequestArguments;

public class BlockingServlet extends HttpServlet {
	private static final long serialVersionUID = -3874017929628817672L;

	protected DataConverters dataConverters;
	protected ComponentManager componentManager;
	protected InterceptorManager interceptorManager;
	protected RequestHandlerManager requestHandlerManager;
	protected ExceptionHandlerManager exceptionHandlerManager;
	
	@Override
	public void init() throws ServletException {
		this.componentManager = ComponentManager.getInstance();
		this.dataConverters = componentManager.getDataConverters();
		this.interceptorManager = componentManager.getInterceptorManager();
		this.requestHandlerManager = componentManager.getRequestHandlerManager();
		this.exceptionHandlerManager = componentManager.getExceptionHandlerManager();
	}
	
	@Override
	protected void doGet(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		handleRequest(HttpMethod.GET, request, response);
	}
	
	@Override
	protected void doPost(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		handleRequest(HttpMethod.POST, request, response);
	}
	
	@Override
	protected void doPut(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		handleRequest(HttpMethod.PUT, request, response);
	}
	
	@Override
	protected void doDelete(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		handleRequest(HttpMethod.DELETE, request, response);
	}
	
	protected void handleRequest(
			HttpMethod method,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		boolean hasHandler = requestHandlerManager.hasHandler(requestURI);
		if(!hasHandler) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			responseString(response, "uri " + requestURI + " not found");
			return;
		}
		RequestHandler requestHandler = requestHandlerManager.getHandler(method, requestURI);
		if(requestHandler == null) {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			responseString(response, "method " + method + " not allowed");
			return;
		}
		RequestArguments arguments = newRequestArguments(method, request, response);
		try {
			boolean passed = preHandleRequest(arguments, requestHandler);
			if(passed) {
				Object responseData = requestHandler.handle(arguments);
				if(responseData != null) {
					String responseContentType = requestHandler.getResponseContentType();
					handleResponseData(response, responseContentType, responseData);
				}
			}
		}
		catch (Exception e) {
			handleException(request, response, e);
		}
		finally {
			arguments.release();
		}
		postHandleRequest(arguments, requestHandler);
	}
	
	protected void handleException(
			HttpServletRequest request,
			HttpServletResponse response, Exception e) throws IOException {
		Class<?> exceptionClass = e.getClass();
		UncaughtExceptionHandler handler = 
				exceptionHandlerManager.getUncaughtExceptionHandler(exceptionClass);
		Exception exception = e;
		if(handler != null) {
			try {
				Object result = handler.handleException(e);
				if(result != null) {
					String responseContentType = handler.getResponseContentType();
					handleResponseData(response, responseContentType, result);
				}
				exception = null;
			}
			catch (Exception ex) {
				exception = ex;
			}
		}
		if(exception != null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			log("handle request uri: " + request.getRequestURI() + " error", exception);
		}
	}
	
	protected boolean preHandleRequest(
			RequestArguments arguments, 
			RequestHandler requestHandler) throws Exception {
		Method handler = requestHandler.getHandlerMethod();
		for(RequestInterceptor interceptor : interceptorManager.getRequestInterceptors()) {
			boolean passed = interceptor.preHandle(arguments, handler);
			if(!passed)
				return false;
		}
		return true;
	}
	
	protected void postHandleRequest(
			RequestArguments arguments, RequestHandler requestHandler) {
		Method handler = requestHandler.getHandlerMethod();
		for(RequestInterceptor interceptor : interceptorManager.getRequestInterceptors())
			interceptor.postHandle(arguments, handler);
	}
	
	@SuppressWarnings({ "rawtypes" })
	protected void handleResponseData(
			HttpServletResponse response, 
			String contentType, Object data) throws Exception {
		response.setContentType(contentType);
		Object body = data;
		if(body instanceof ResponseEntity) {
			ResponseEntity entity = (ResponseEntity)body;
			body = entity.getBody();
			response.setStatus(entity.getStatus());
			MultiValueMap headers = entity.getHeaders();
			if(headers != null) {
				Map<String, String> encodedHeaders = headers.encode();
				for(Entry<String, String> entry : encodedHeaders.entrySet())
					response.addHeader(entry.getKey(), entry.getValue());
			}
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
		}
		if(body != null)
			responseBody(response, body);
	}
	
	protected void responseBody(
			HttpServletResponse response, Object data) throws IOException {
		String contentType = response.getContentType();
		BodySerializer bodySerializer = dataConverters.getBodySerializer(contentType);
		if(bodySerializer == null)
			throw new IOException("has no body serializer for: " + contentType);
		byte[] bytes = bodySerializer.serialize(data);
		responseBytes(response, bytes);
	}
	
	protected void responseString(
			HttpServletResponse response, String str) throws IOException {
		byte[] bytes = EzyStrings.getUtfBytes(str);
		responseBytes(response, bytes);
	}
	
	protected void responseBytes(
			HttpServletResponse response, byte[] bytes) throws IOException {
		response.setContentLength(bytes.length);
		ServletOutputStream outputStream = response.getOutputStream();
		outputStream.write(bytes);
	}
	
	protected RequestArguments newRequestArguments(
			HttpMethod method,
			HttpServletRequest request, 
			HttpServletResponse response) {
		SimpleRequestArguments arguments = new SimpleRequestArguments();
		arguments.setMethod(method);
		arguments.setRequest(request);
		arguments.setResponse(response);
		
		Enumeration<String> paramNames = request.getParameterNames();
		while(paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			String paramValue = request.getParameter(paramName);
			arguments.setParameter(paramName, paramValue);
		}
		
		Enumeration<String> headerNames = request.getHeaderNames();
		while(headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			String headerValue = request.getHeader(headerName);
			arguments.setHeader(headerName, headerValue);
		}
		
		return arguments;
	}

}
