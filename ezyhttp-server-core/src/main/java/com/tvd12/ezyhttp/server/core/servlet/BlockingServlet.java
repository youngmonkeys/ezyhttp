package com.tvd12.ezyhttp.server.core.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.reflect.EzyClassTree;
import com.tvd12.ezyfox.sercurity.EzyBase64;
import com.tvd12.ezyhttp.core.codec.BodySerializer;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.data.MultiValueMap;
import com.tvd12.ezyhttp.core.exception.DeserializeValueException;
import com.tvd12.ezyhttp.core.exception.HttpRequestException;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.constant.CoreConstants;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.handler.RequestResponseWatcher;
import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;
import com.tvd12.ezyhttp.server.core.handler.UnhandledErrorHandler;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.manager.ExceptionHandlerManager;
import com.tvd12.ezyhttp.server.core.manager.InterceptorManager;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.core.manager.RequestURIManager;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.request.SimpleRequestArguments;
import com.tvd12.ezyhttp.server.core.view.Redirect;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewContext;

public class BlockingServlet extends HttpServlet {
	private static final long serialVersionUID = -3874017929628817672L;

	private boolean debug;
	private int managmentPort;
	private boolean exposeMangementURIs;
	protected ViewContext viewContext;
	protected ObjectMapper objectMapper;
	protected DataConverters dataConverters;
	protected ComponentManager componentManager;
	protected InterceptorManager interceptorManager;
	protected RequestURIManager requestURIManager;
	protected RequestHandlerManager requestHandlerManager;
	protected ExceptionHandlerManager exceptionHandlerManager;
	protected UnhandledErrorHandler unhandledErrorHandler;
	protected List<Class<?>> handledExceptionClasses;
	protected List<RequestResponseWatcher> requestResponseWatchers;
	protected Map<Class<?>, UncaughtExceptionHandler> uncaughtExceptionHandlers;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void init() throws ServletException {
		this.componentManager = ComponentManager.getInstance();
		this.debug = componentManager.isDebug();
		this.managmentPort = componentManager.getManagmentPort();
		this.exposeMangementURIs = componentManager.isExposeMangementURIs();
		this.viewContext = componentManager.getViewContext();
		this.objectMapper = componentManager.getObjectMapper();
		this.dataConverters = componentManager.getDataConverters();
		this.interceptorManager = componentManager.getInterceptorManager();
		this.requestHandlerManager = componentManager.getRequestHandlerManager();
		this.requestURIManager = requestHandlerManager.getRequestURIManager();
		this.exceptionHandlerManager = componentManager.getExceptionHandlerManager();
		this.requestResponseWatchers = componentManager.getRequestResponseWatchers();
		this.addDefaultExceptionHandlers();
		this.unhandledErrorHandler = componentManager.getUnhandledErrorHandler();
		this.uncaughtExceptionHandlers = exceptionHandlerManager.getUncaughtExceptionHandlers();
		this.handledExceptionClasses = new EzyClassTree(uncaughtExceptionHandlers.keySet()).toList();

	}

	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	    doHandleRequest(HttpMethod.GET, request, response);
	}

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	    doHandleRequest(HttpMethod.POST, request, response);
	}

	@Override
	protected void doPut(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	    doHandleRequest(HttpMethod.PUT, request, response);
	}

	@Override
	protected void doDelete(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	    doHandleRequest(HttpMethod.DELETE, request, response);
	}

	private void doHandleRequest(
            HttpMethod method,
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
            watchRequest(method, request, response);
            handleRequest(method, request, response);
        }
        finally {
            if (!request.isAsyncStarted()) {
                watchResponse(method, request, response);
            }
        }
    }

	private void watchRequest(
            HttpMethod method,
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
	    for (RequestResponseWatcher watcher : requestResponseWatchers) {
	        watcher.watchRequest(method, request);
	    }
    }

	private void watchResponse(
            HttpMethod method,
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        for (RequestResponseWatcher watcher : requestResponseWatchers) {
            watcher.watchResponse(method, request, response);
        }
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
		String matchedURI = requestHandlerManager.getMatchedURI(method, requestURI);
		if(matchedURI == null) {
            if (!handleError(method, request, response, HttpServletResponse.SC_NOT_FOUND)) {
                responseString(response, "uri " + requestURI + " not found");
            }
            return;
        }
		request.setAttribute(CoreConstants.ATTRIBUTE_MATCHED_URI, matchedURI);
		boolean isManagementURI = requestURIManager.isManagementUri(matchedURI);
		if(isManagementURI 
		    && !exposeMangementURIs 
		    && request.getServerPort() != managmentPort
        ) {
		    handleError(method, request, response, HttpServletResponse.SC_NOT_FOUND);
			logger.warn("a normal client's not allowed call to: {}, please check your proxy configuration", requestURI);
			return;
		}
		RequestHandler requestHandler =
		        requestHandlerManager.getHandler(method, matchedURI, isManagementURI);
		if(requestHandler == RequestHandler.EMPTY) {
		    if (!handleError(method, request, response, HttpServletResponse.SC_METHOD_NOT_ALLOWED)) {
		        responseString(response, "method " + method + " not allowed");
		    }
			return;
		}
		boolean acceptableRequest = false;
		boolean syncResponse = true;
		String uriTemplate = requestHandler.getRequestURI();
		RequestArguments arguments = newRequestArguments(method, uriTemplate, request, response);
		try {
			acceptableRequest = preHandleRequest(arguments, requestHandler);
			if(acceptableRequest) {
			    if (requestHandler.isAsync()) {
			        syncResponse = false;
                    AsyncContext asyncContext = request.startAsync(request, response);
                    asyncContext.addListener(newAsyncListener(arguments, requestHandler));
                    requestHandler.handle(arguments);
			    }
			    else {
    				Object responseData = requestHandler.handle(arguments);
    				String responseContentType = requestHandler.getResponseContentType();
    				if(responseContentType != null) {
    					response.setContentType(responseContentType);
    				}
    				if(responseData != null) {
    				    handleResponseData(request, response, responseData);
    				}
    				else {
    				    response.setStatus(HttpServletResponse.SC_OK);
    				}
			    }
			}
			else {
			    handleError(method, request, response, HttpServletResponse.SC_NOT_ACCEPTABLE);
			}
		}
		catch (Exception e) {
			handleException(method, arguments, e);
		}
		finally {
		    if (syncResponse) {
		        if (acceptableRequest) {
		            postHandleRequest(arguments, requestHandler);
		        }
		        arguments.release();
		    }
		}
	}

	protected AsyncListener newAsyncListener(
	        RequestArguments arguments, RequestHandler requestHandler) {
	    return new AsyncCallback() {

            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                try {
                    try {
                        postHandleRequest(arguments, requestHandler);
                    }
                    finally {
                        watchResponse(
                            arguments.getMethod(),
                            arguments.getRequest(),
                            arguments.getResponse()
                        );
                    }
                }
                catch (Exception e) {
                    logger.warn(
                        "AsyncCallback.onComplete on uri: {} error",
                        arguments.getRequest().getRequestURI(), e
                    );
                }
                finally {
                    arguments.release();
                }
            }
        };
	}

	protected boolean handleError(
        HttpMethod method,
        HttpServletRequest request,
        HttpServletResponse response,
        int errorStatusCode
    ) {
	    return handleError(method, request, response, errorStatusCode, null);
	}

	protected boolean handleError(
        HttpMethod method,
        HttpServletRequest request,
        HttpServletResponse response,
        int errorStatusCode,
        Exception exception
    ) {
		if (exception != null) {
			logger.warn("handle request uri: {} error", request.getRequestURI(), exception);
		}
		if (unhandledErrorHandler != null) {
            Object data = unhandledErrorHandler.handleError(
                method,
                request,
                response,
                errorStatusCode,
                exception
            );
	        if (data == null) {
	            response.setStatus(errorStatusCode);
	            return false;
	        }
	        try {
                handleResponseData(request, response, data);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                logger.warn("handle error: {} with uri: {} failed", errorStatusCode, request.getRequestURI(), e);
            }
            return true;
	    } else {
	        response.setStatus(errorStatusCode);
	        return false;
	    }
	}

	protected void handleException(
	        HttpMethod method,
			RequestArguments arguments,
			Exception e
	) throws IOException {
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
				else {
				    handleError(method, request, response, HttpServletResponse.SC_BAD_REQUEST);
				}
				exception = null;
			}
			catch (Exception ex) {
				exception = ex;
			}
		}
		if(exception != null) {
		    handleError(method, request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception);
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
			for(Cookie cookie : redirect.getCookies())
				response.addCookie(cookie);
			for(Entry<String, String> e : redirect.getHeaders().entrySet())
				response.addHeader(e.getKey(), e.getValue());
			Map<String, Object> attributes = redirect.getAttributes();
			if (attributes != null) {
			    String attributesValue = objectMapper.writeValueAsString(attributes);
			    Cookie attributesCookie = new Cookie(
			            CoreConstants.COOKIE_REDIRECT_ATTRIBUTES_NAME,
			            EzyBase64.encodeUtf(attributesValue));
			    attributesCookie.setMaxAge(CoreConstants.COOKIE_REDIRECT_ATTRIBUTES_MAX_AGE);
			    response.addCookie(attributesCookie);
			}
			response.sendRedirect(redirect.getUri() + redirect.getQueryString());
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
			for(Cookie cookie : view.getCookies())
				response.addCookie(cookie);
			for(Entry<String, String> e : view.getHeaders().entrySet())
				response.addHeader(e.getKey(), e.getValue());
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
		arguments.setDebug(debug);
		arguments.setMethod(method);
		arguments.setRequest(request);
		arguments.setResponse(response);
		arguments.setUriTemplate(uriTemplate);
		arguments.setCookies(request.getCookies());
		arguments.setObjectMapper(objectMapper);
		arguments.setRedirectionAttributesFromCookie();

		Enumeration<String> paramNames = request.getParameterNames();
		while(paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			arguments.setParameter(paramName, paramValues);
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
