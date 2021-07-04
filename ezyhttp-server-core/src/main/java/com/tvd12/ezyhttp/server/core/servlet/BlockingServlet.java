package com.tvd12.ezyhttp.server.core.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.reflect.EzyClassTree;
import com.tvd12.ezyhttp.core.codec.BodySerializer;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.data.MultiValueMap;
import com.tvd12.ezyhttp.core.exception.DeserializeValueException;
import com.tvd12.ezyhttp.core.exception.HttpRequestException;
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
import com.tvd12.ezyhttp.server.core.view.Redirect;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewContext;

public class BlockingServlet extends HttpServlet {
	private static final long serialVersionUID = -3874017929628817672L;

	private int serverPort;
	private int managmentPort;
	private Set<String> managementURIs;
	protected ViewContext viewContext;
	protected DataConverters dataConverters;
	protected ComponentManager componentManager;
	protected InterceptorManager interceptorManager;
	protected RequestHandlerManager requestHandlerManager;
	protected ExceptionHandlerManager exceptionHandlerManager;
	protected List<Class<?>> handledExceptionClasses;
	protected Map<Class<?>, UncaughtExceptionHandler> uncaughtExceptionHandlers;
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void init() throws ServletException {
		this.componentManager = ComponentManager.getInstance();
		this.serverPort = componentManager.getServerPort();
		this.managmentPort = componentManager.getManagmentPort();
		this.managementURIs = componentManager.getManagementURIs();
		this.viewContext = componentManager.getViewContext();
		this.dataConverters = componentManager.getDataConverters();
		this.interceptorManager = componentManager.getInterceptorManager();
		this.requestHandlerManager = componentManager.getRequestHandlerManager();
		this.exceptionHandlerManager = componentManager.getExceptionHandlerManager();
		this.addDefaultExceptionHandlers();
		this.uncaughtExceptionHandlers = exceptionHandlerManager.getUncaughtExceptionHandlers();
		this.handledExceptionClasses = new EzyClassTree(uncaughtExceptionHandlers.keySet()).toList();
		
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
	
	protected void preHandleRequest(
			HttpMethod method,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
	}
	
	protected void handleRequest(
			HttpMethod method,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		preHandleRequest(method, request, response);
		String requestURI = request.getRequestURI();
		if(managementURIs.contains(requestURI)) {
			if(request.getServerPort() == serverPort) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				logger.warn("a normal client's not allowed call to: {}, please check your proxy configuration", requestURI);
				return;
			}
		}
		else {
			if(request.getServerPort() == managmentPort) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				logger.warn("management server ({}) not allowed call to: {}, please check it", request.getRemoteHost(), requestURI);
				return;
			}
		}
		RequestHandler requestHandler = requestHandlerManager.getHandler(method, requestURI);
		if(requestHandler == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			responseString(response, "uri " + requestURI + " not found");
			return;
		}
		if(requestHandler == RequestHandler.EMPTY) {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			responseString(response, "method " + method + " not allowed");
			return;
		}
		boolean acceptableRequest = false;
		String uriTemplate = requestHandler.getRequestURI();
		RequestArguments arguments = newRequestArguments(method, uriTemplate, request, response);
		try {
			acceptableRequest = preHandleRequest(arguments, requestHandler);
			if(acceptableRequest) {
				Object responseData = requestHandler.handle(arguments);
				String responseContentType = requestHandler.getResponseContentType();
				if(responseContentType != null) {
					response.setContentType(responseContentType);
				}
				if(responseData != null) {
					if(responseData == ResponseEntity.ASYNC) {
						request.startAsync();
					}
					else {
						handleResponseData(request, response, responseData);
					}
				}
			}
			else {
				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			}
		}
		catch (Exception e) {
			handleException(arguments, e);
		}
		finally {
			arguments.release();
		}
		if(acceptableRequest)
			postHandleRequest(arguments, requestHandler);
	}
	
	protected void handleException(
			RequestArguments arguments, Exception e) throws IOException {
		UncaughtExceptionHandler handler = getUncaughtExceptionHandler(e.getClass());
		HttpServletRequest request = arguments.getRequest();
		HttpServletResponse response = arguments.getResponse();
		Exception exception = e;
		if(handler != null) {
			try {
				Object result = handler.handleException(arguments, e);
				if(result != null) {
					String responseContentType = handler.getResponseContentType();
					if(responseContentType != null) {
						response.setContentType(responseContentType);
					}
					handleResponseData(request, response, result);
				}
				exception = null;
			}
			catch (Exception ex) {
				exception = ex;
			}
		}
		if(exception != null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.warn("handle request uri: {} error", request.getRequestURI(), exception);
		}
	}
	
	protected UncaughtExceptionHandler getUncaughtExceptionHandler(Class<?> exceptionClass) {
		for(Class<?> exc : handledExceptionClasses) {
			if(exc.isAssignableFrom(exceptionClass)) {
				return uncaughtExceptionHandlers.get(exc);
			}
		}
		return null;
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
	
	protected void handleResponseData(
			HttpServletRequest request,
			HttpServletResponse response, Object data) throws Exception {
		Object body = data;
		if(data instanceof ResponseEntity) {
			ResponseEntity entity = (ResponseEntity)body;
			body = entity.getBody();
			response.setStatus(entity.getStatus());
			MultiValueMap headers = entity.getHeaders();
			if(headers != null) {
				Map<String, String> encodedHeaders = headers.toMap();
				for(Entry<String, String> entry : encodedHeaders.entrySet())
					response.addHeader(entry.getKey(), entry.getValue());
			}
		}
		else if(data instanceof Redirect) {
			Redirect redirect = (Redirect)data;
			response.sendRedirect(redirect.getUri());
			return;
		}
		else if(data instanceof View) {
			if(viewContext == null) {
				throw new IllegalStateException(
					"viewContext is null, " +
					"you must add ezyhttp-server-thymeleaf to your dependencies" +
					" or create viewContext by yourself"
				);
			}
			View view = (View)data;
			response.setContentType(view.getContentType());
			viewContext.render(getServletContext(), request, response, view);
			return;
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
			String uriTemplate,
			HttpServletRequest request, 
			HttpServletResponse response) {
		SimpleRequestArguments arguments = new SimpleRequestArguments();
		arguments.setMethod(method);
		arguments.setRequest(request);
		arguments.setResponse(response);
		arguments.setUriTemplate(uriTemplate);
		
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
	
	protected void addDefaultExceptionHandlers() {
		exceptionHandlerManager.addUncaughtExceptionHandler(
				DeserializeValueException.class, 
				new UncaughtExceptionHandler() {
					@Override
					public Object handleException(RequestArguments args, Exception e) throws Exception {
						DeserializeValueException deException = (DeserializeValueException)e;
						Map<String, String> errorData = new HashMap<>();
						errorData.put(deException.getValueName(), "invalid");
						errorData.put("exception", e.getClass().getName());
						return ResponseEntity.create(StatusCodes.BAD_REQUEST, errorData);
					}
				});
		exceptionHandlerManager.addUncaughtExceptionHandler(
				HttpRequestException.class, 
				new UncaughtExceptionHandler() {
					@Override
					public Object handleException(RequestArguments args, Exception e) throws Exception {
						HttpRequestException requestException = (HttpRequestException)e;
						int errorStatus = requestException.getCode();
						Object errorData = requestException.getData();
						if(errorData == null)
							errorData = Collections.emptyMap();
						return ResponseEntity.create(errorStatus, errorData);
					}
				});
	}

}
