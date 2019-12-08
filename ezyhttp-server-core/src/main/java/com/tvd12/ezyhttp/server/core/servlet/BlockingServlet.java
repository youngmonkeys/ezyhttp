package com.tvd12.ezyhttp.server.core.servlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

public class BlockingServlet extends HttpServlet {
	private static final long serialVersionUID = -3874017929628817672L;

	protected ObjectMapper objectMapper;
	protected RequestHandlerManager requestHandlerManager;
	
	@Override
	public void init() throws ServletException {
		objectMapper = new ObjectMapper();
		ComponentManager componentManager = ComponentManager.getInstance();
		requestHandlerManager = componentManager.getRequestHandlerManager();
	}
	
	@Override
	protected void doGet(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		RequestHandler requestHandler = requestHandlerManager.getHandler(requestURI);
		String responseContentType = requestHandler.getResponseContentType();
		RequestArguments arguments = newRequestArguments(request, response);
		try {
			Object responseData = requestHandler.handle(arguments);
			response.setContentType(responseContentType);
			response.setStatus(HttpServletResponse.SC_OK);
			responseData(response, responseData);
		}
		catch (Exception e) {
			response.setContentType(responseContentType);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseString(response, "Internal Server Error");
		}
		finally {
			arguments.release();
		}
	}
	
	protected void responseData(
			HttpServletResponse response, Object data) throws IOException {
		byte[] bytes = objectMapper.writeValueAsBytes(data);
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
		arguments.setArgument(HttpServletRequest.class, request);
		arguments.setArgument(HttpServletResponse.class, response);
		
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
