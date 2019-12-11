package com.tvd12.ezyhttp.server.core.servlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.server.core.codec.BodySerializer;
import com.tvd12.ezyhttp.server.core.codec.DataConverters;
import com.tvd12.ezyhttp.server.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.manager.ExceptionHandlerManager;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

public class BlockingServlet extends HttpServlet {
	private static final long serialVersionUID = -3874017929628817672L;

	protected DataConverters dataConverters;
	protected ComponentManager componentManager;
	protected RequestHandlerManager requestHandlerManager;
	protected ExceptionHandlerManager exceptionHandlerManager;
	
	@Override
	public void init() throws ServletException {
		this.componentManager = ComponentManager.getInstance();
		this.dataConverters = componentManager.getDataConverters();
		this.requestHandlerManager = componentManager.getRequestHandlerManager();
		this.exceptionHandlerManager = componentManager.getExceptionHandlerManager();
	}
	
	@Override
	protected void doGet(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response, HttpMethod.GET);
	}
	
	@Override
	protected void doPost(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response, HttpMethod.POST);
	}
	
	@Override
	protected void doPut(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response, HttpMethod.PUT);
	}
	
	@Override
	protected void doDelete(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response, HttpMethod.DELETE);
	}
	
	protected void handleRequest(
			HttpServletRequest request, 
			HttpServletResponse response,
			HttpMethod method) throws ServletException, IOException {
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
		RequestArguments arguments = newRequestArguments(request, response);
		try {
			Object responseData = requestHandler.handle(arguments);
			if(responseData != null) {
				String responseContentType = requestHandler.getResponseContentType();
				handleResponseData(response, responseContentType, responseData);
			}
		}
		catch (Exception e) {
			handleException(request, response, e);
		}
		finally {
			arguments.release();
		}
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
	
	protected void handleResponseData(
			HttpServletResponse response, 
			String contentType, Object data) throws Exception {
		response.setContentType(contentType);
		response.setStatus(HttpServletResponse.SC_OK);
		responseData(response, data);
	}
	
	protected void responseData(
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
			HttpServletRequest request, 
			HttpServletResponse response) {
		RequestArguments arguments = new RequestArguments();
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
