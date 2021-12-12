package com.tvd12.ezyhttp.server.core.test.servlet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.util.EzyWrap;
import com.tvd12.ezyhttp.core.codec.BodySerializer;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.DeserializeValueException;
import com.tvd12.ezyhttp.core.exception.HttpRequestException;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.RequestArgument;
import com.tvd12.ezyhttp.server.core.annotation.RequestCookie;
import com.tvd12.ezyhttp.server.core.constant.CoreConstants;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.handler.RequestResponseWatcher;
import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;
import com.tvd12.ezyhttp.server.core.handler.UnhandledErrorHandler;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.manager.ExceptionHandlerManager;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.request.RequestURI;
import com.tvd12.ezyhttp.server.core.servlet.BlockingServlet;
import com.tvd12.ezyhttp.server.core.view.Redirect;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewContext;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class BlockingServletTest {

	private static final int PORT = 8080;
	private static final int MANAGEMENT_POR = 18080;
	private static final RequestController CONTROLLER = new RequestController();
	
	@Test
	public void doGetTest() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		RequestResponseWatcher watcher = mock(RequestResponseWatcher.class);
		componentManager.addRequestResponseWatchers(Arrays.asList(watcher));
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		when(request.getParameterValues("param")).thenReturn(new String[] {"ParameterValue"});
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		GetRequestHandler requestHandler = new GetRequestHandler();
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, false), requestHandler);
		
		RequestInterceptor interceptor = mock(RequestInterceptor.class);
		when(interceptor.preHandle(any(), any())).thenReturn(true);
		componentManager.getInterceptorManager().addRequestInterceptors(Arrays.asList(interceptor));
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).getContentType();
		verify(response, times(1)).getOutputStream();
		verify(response, times(1)).setStatus(StatusCodes.OK);
		
		DataConverters dataConverters = componentManager.getDataConverters();
		BodySerializer bodySerializer = dataConverters.getBodySerializer(ContentTypes.APPLICATION_JSON);
		ExResponse responseData = new ExResponse(
			"Hello ParameterValue, HeaderValue, CookieValue"
		);
		verify(outputStream, times(1)).write(bodySerializer.serialize(responseData));
		
		verify(interceptor, times(1)).preHandle(any(), any());
		verify(interceptor, times(1)).postHandle(any(), any());
		verify(watcher, times(1)).watchRequest(HttpMethod.GET, request);
		verify(watcher, times(1)).watchResponse(HttpMethod.GET, request, response);
		
		componentManager.destroy();
	}
	
	@Test
	public void doGetManagementTest() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Sets.newHashSet("/get"));
		componentManager.setServerPort(PORT);
		componentManager.setManagmentPort(MANAGEMENT_POR);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(MANAGEMENT_POR);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		when(request.getParameterValues("param")).thenReturn(new String[] {"ParameterValue"});
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		GetRequestHandler requestHandler = new GetRequestHandler();
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, true), requestHandler);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).getContentType();
		verify(response, times(1)).getOutputStream();
		verify(response, times(1)).setStatus(StatusCodes.OK);
		
		DataConverters dataConverters = componentManager.getDataConverters();
		BodySerializer bodySerializer = dataConverters.getBodySerializer(ContentTypes.APPLICATION_JSON);
		ExResponse responseData = new ExResponse(
			"Hello ParameterValue, HeaderValue, CookieValue"
		);
		verify(outputStream, times(1)).write(bodySerializer.serialize(responseData));
		
		componentManager.destroy();
	}
	
	@Test
	public void doGetToManagementNotAllow() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Sets.newHashSet("/management"));
		componentManager.setServerPort(PORT);
		componentManager.setManagmentPort(MANAGEMENT_POR);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/management";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).setStatus(StatusCodes.NOT_FOUND);

		componentManager.destroy();
	}
	
	@Test
	public void doGetFromManagement() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Sets.newHashSet("/management"));
		componentManager.setServerPort(PORT);
		componentManager.setManagmentPort(MANAGEMENT_POR);
		componentManager.getRequestHandlerManager().addHandler(
	        new RequestURI(HttpMethod.GET, "/get", false),
	        mock(RequestHandler.class)
		);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(MANAGEMENT_POR);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).setStatus(StatusCodes.NOT_FOUND);

		componentManager.destroy();
	}
	
	@Test
    public void doGetFromManagementButExpose() throws Exception {
        // given
        ComponentManager componentManager = ComponentManager.getInstance();
        componentManager.addManagementURIs(Sets.newHashSet("/management"));
        componentManager.setServerPort(PORT);
        componentManager.setManagmentPort(MANAGEMENT_POR);
        componentManager.setExposeMangementURIs(true);
        componentManager.getRequestHandlerManager().addHandler(
            new RequestURI(HttpMethod.GET, "/management", false),
            mock(RequestHandler.class)
        );
        
        BlockingServlet sut = new BlockingServlet();
        sut.init();
        
        String requestURI = "/management";
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(request.getRequestURI()).thenReturn(requestURI);
        when(request.getServerPort()).thenReturn(MANAGEMENT_POR);
        
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // when
        sut.service(request, response);
        
        // then
        verify(request, times(1)).getMethod();
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getServerPort();
        
        verify(response, times(1)).setStatus(StatusCodes.NOT_FOUND);

        componentManager.destroy();
    }
	
	@Test
	public void requestHandlerNull() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get-handler-null";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		
		verify(response, times(1)).getOutputStream();
		verify(response, times(1)).setStatus(StatusCodes.NOT_FOUND);
		
		verify(outputStream, times(1)).write("uri /get-handler-null not found".getBytes());
		
		componentManager.destroy();
	}
	
	@Test
    public void requestHandlerNullAndHasErrorHandler() throws Exception {
        // given
        ComponentManager componentManager = ComponentManager.getInstance();
        componentManager.addManagementURIs(Collections.emptySet());
        componentManager.setServerPort(PORT);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        UnhandledErrorHandler unhandledErrorHandler = mock(UnhandledErrorHandler.class);
        ResponseEntity responseEntity = ResponseEntity.ok();
        when(
            unhandledErrorHandler.handleError(
                HttpMethod.GET,
                request,
                response,
                HttpServletResponse.SC_NOT_FOUND,
				null
            )
        ).thenReturn(responseEntity);
        componentManager.setUnhandledErrorHandler(Arrays.asList(unhandledErrorHandler));
        
        BlockingServlet sut = new BlockingServlet();
        sut.init();
        
        String requestURI = "/get-handler-null";
        
        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(request.getRequestURI()).thenReturn(requestURI);
        when(request.getServerPort()).thenReturn(PORT);
        
        when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
        
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        
        // when
        sut.service(request, response);
        
        // then
        verify(request, times(1)).getMethod();
        verify(request, times(1)).getRequestURI();
        
        verify(response, times(1)).setStatus(StatusCodes.OK);
        
        componentManager.destroy();
    }
	
	@Test
	public void requestHandlerEmpty() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		GetRequestHandler requestHandler = new GetRequestHandler();
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.POST, requestURI, false), requestHandler);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).getOutputStream();
		verify(response, times(1)).setStatus(StatusCodes.METHOD_NOT_ALLOWED);
		
		verify(outputStream, times(1)).write("method GET not allowed".getBytes());
		
		componentManager.destroy();
	}
	
	@Test
    public void requestHandlerEmptyAndHasErrorHandler() throws Exception {
        // given
        ComponentManager componentManager = ComponentManager.getInstance();
        componentManager.addManagementURIs(Collections.emptySet());
        componentManager.setServerPort(PORT);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        UnhandledErrorHandler unhandledErrorHandler = mock(UnhandledErrorHandler.class);
        ResponseEntity responseEntity = ResponseEntity.ok();
        when(
            unhandledErrorHandler.handleError(
                HttpMethod.GET,
                request,
                response,
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
				null
            )
        ).thenReturn(responseEntity);
        componentManager.setUnhandledErrorHandler(Arrays.asList(unhandledErrorHandler));
        

        BlockingServlet sut = new BlockingServlet();
        sut.init();
        
        String requestURI = "/get";
        
        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(request.getRequestURI()).thenReturn(requestURI);
        when(request.getServerPort()).thenReturn(PORT);
        
        when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
        
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        
        RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
        GetRequestHandler requestHandler = new GetRequestHandler();
        requestHandlerManager.addHandler(new RequestURI(HttpMethod.POST, requestURI, false), requestHandler);
        
        // when
        sut.service(request, response);
        
        // then
        verify(request, times(1)).getMethod();
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getServerPort();
        
        verify(response, times(1)).setStatus(StatusCodes.OK);
        
        componentManager.destroy();
    }
	
	@Test
    public void requestHandlerEmptyWithErrorHandlerButDataNull() throws Exception {
        // given
        ComponentManager componentManager = ComponentManager.getInstance();
        componentManager.addManagementURIs(Collections.emptySet());
        componentManager.setServerPort(PORT);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        UnhandledErrorHandler unhandledErrorHandler = mock(UnhandledErrorHandler.class);
        when(
            unhandledErrorHandler.handleError(
                HttpMethod.GET,
                request,
                response,
                HttpServletResponse.SC_NOT_FOUND,
				null
            )
        ).thenReturn(null);
        componentManager.setUnhandledErrorHandler(Arrays.asList(unhandledErrorHandler));
        
        BlockingServlet sut = new BlockingServlet();
        sut.init();
        
        String requestURI = "/get";
        
        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(request.getRequestURI()).thenReturn(requestURI);
        when(request.getServerPort()).thenReturn(PORT);
        
        when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
        
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        
        RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
        GetRequestHandler requestHandler = new GetRequestHandler();
        requestHandlerManager.addHandler(new RequestURI(HttpMethod.POST, requestURI, false), requestHandler);
        
        // when
        sut.service(request, response);
        
        // then
        verify(request, times(1)).getMethod();
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getServerPort();
        
        verify(response, times(1)).getOutputStream();
        verify(response, times(1)).setStatus(StatusCodes.METHOD_NOT_ALLOWED);
        
        verify(outputStream, times(1)).write("method GET not allowed".getBytes());
        
        componentManager.destroy();
    }
	
	@Test
    public void requestHandlerEmptyAndHasErrorHandlerButException() throws Exception {
        // given
        ComponentManager componentManager = ComponentManager.getInstance();
        componentManager.addManagementURIs(Collections.emptySet());
        componentManager.setServerPort(PORT);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        UnhandledErrorHandler unhandledErrorHandler = mock(UnhandledErrorHandler.class);
        ResponseEntity responseEntity = ResponseEntity.ok();
        when(
            unhandledErrorHandler.handleError(
                HttpMethod.GET,
                request,
                response,
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
				null
            )
        ).thenReturn(responseEntity);
        componentManager.setUnhandledErrorHandler(Arrays.asList(unhandledErrorHandler));
        

        BlockingServlet sut = new BlockingServlet();
        sut.init();
        
        String requestURI = "/get";
        
        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(request.getRequestURI()).thenReturn(requestURI);
        when(request.getServerPort()).thenReturn(PORT);
        
        when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
        doThrow(
            new IllegalStateException("just test")
        ).when(response).setStatus(StatusCodes.OK);
        
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        
        RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
        GetRequestHandler requestHandler = new GetRequestHandler();
        requestHandlerManager.addHandler(new RequestURI(HttpMethod.POST, requestURI, false), requestHandler);
        
        // when
        sut.service(request, response);
        
        // then
        verify(request, times(1)).getMethod();
        verify(request, times(2)).getRequestURI();
        verify(request, times(1)).getServerPort();
        
        verify(response, times(1)).setStatus(StatusCodes.OK);
        verify(response, times(1)).setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
        
        componentManager.destroy();
    }
	
	@Test
	public void doGetNotAcceptable() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		when(request.getParameterValues("param")).thenReturn(new String[] {"ParameterValue"});
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		GetRequestHandler requestHandler = new GetRequestHandler();
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, false), requestHandler);
		
		RequestInterceptor interceptor = mock(RequestInterceptor.class);
		when(interceptor.preHandle(any(), any())).thenReturn(false);
		componentManager.getInterceptorManager().addRequestInterceptors(Arrays.asList(interceptor));
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).setStatus(StatusCodes.NOT_ACCEPTABLE);
		
		verify(interceptor, times(1)).preHandle(any(), any());
		
		componentManager.destroy();
	}
	
	@Test
	public void doGetResponseContentTypeNull() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		when(request.getParameterValues("param")).thenReturn(new String[] {"ParameterValue"});
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
        
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        
        AsyncContext asyncContext = mock(AsyncContext.class);
        when(request.startAsync(request, response)).thenReturn(asyncContext);
        when(request.isAsyncStarted()).thenReturn(true);
        EzyWrap<AsyncListener> asyncListener = new EzyWrap<>();
        doAnswer(it -> {
            asyncListener.setValue(it.getArgumentAt(0, AsyncListener.class));
            return null;
        }).when(asyncContext).addListener(any(AsyncListener.class));
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		GetRequestHandlerContentTypeNull requestHandler = new GetRequestHandlerContentTypeNull();
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, false), requestHandler);
		
		// when
		sut.service(request, response);
		asyncListener.getValue().onComplete(new AsyncEvent(asyncContext));
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		verify(asyncContext, times(1)).addListener(any(AsyncListener.class));
		
		componentManager.destroy();
	}
	
	@Test
    public void doGetResponseContentTypeNullAndPostHandleRequestError() throws Exception {
        // given
        ComponentManager componentManager = ComponentManager.getInstance();
        componentManager.addManagementURIs(Collections.emptySet());
        componentManager.setServerPort(PORT);
        
        BlockingServlet sut = new BlockingServlet();
        sut.init();
        
        String requestURI = "/get";
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(request.getRequestURI()).thenReturn(requestURI);
        when(request.getServerPort()).thenReturn(PORT);
        when(request.getParameterNames()).thenReturn(
            Collections.enumeration(Arrays.asList("param"))
        );
        when(request.getParameter("param")).thenReturn("ParameterValue");
        when(request.getParameterValues("param")).thenReturn(new String[] {"ParameterValue"});
        
        when(request.getHeaderNames()).thenReturn(
            Collections.enumeration(Arrays.asList("header"))
        );
        when(request.getHeader("header")).thenReturn("HeaderValue");
        
        when(request.getCookies()).thenReturn(
            new Cookie[] { new Cookie("cookie", "CookieValue") }
        );
        
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
        
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        
        AsyncContext asyncContext = mock(AsyncContext.class);
        when(request.startAsync(request, response)).thenReturn(asyncContext);
        when(request.isAsyncStarted()).thenReturn(true);
        EzyWrap<AsyncListener> asyncListener = new EzyWrap<>();
        doAnswer(it -> {
            asyncListener.setValue(it.getArgumentAt(0, AsyncListener.class));
            return null;
        }).when(asyncContext).addListener(any(AsyncListener.class));
        
        RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
        GetRequestHandlerContentTypeNull requestHandler = new GetRequestHandlerContentTypeNull();
        requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, false), requestHandler);
        
        // when
        sut.service(request, response);
        
        RequestInterceptor interceptor = mock(RequestInterceptor.class);
        doThrow(IllegalStateException.class).when(interceptor).postHandle(any(), any());
        componentManager.getInterceptorManager().addRequestInterceptors(Arrays.asList(interceptor));
        asyncListener.getValue().onComplete(new AsyncEvent(asyncContext));
        
        // then
        verify(request, times(1)).getMethod();
        verify(request, times(2)).getRequestURI();
        verify(request, times(1)).getServerPort();
        verify(asyncContext, times(1)).addListener(any(AsyncListener.class));
        
        componentManager.destroy();
    }
	
	@Test
	public void responseDataIsNull() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		GetRequestDataNullHandler requestHandler = new GetRequestDataNullHandler();
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, false), requestHandler);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).setStatus(StatusCodes.OK);
		
		componentManager.destroy();
	}
	
	@Test
	public void getRequestDeserializeValueException() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		GetRequestDeserializeValueExceptionHandler requestHandler = new GetRequestDeserializeValueExceptionHandler();
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, false), requestHandler);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).getContentType();
		verify(response, times(1)).getOutputStream();
		verify(response, times(1)).setStatus(StatusCodes.BAD_REQUEST);
		
		componentManager.destroy();
	}
	
	@Test
	public void getRequestHttpRequestExceptionDataNotNull() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		GetRequestHttpRequestExceptionnHandler requestHandler = new GetRequestHttpRequestExceptionnHandler("hello");
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, false), requestHandler);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).getContentType();
		verify(response, times(1)).getOutputStream();
		verify(response, times(1)).setStatus(StatusCodes.BAD_REQUEST);
		
		componentManager.destroy();
	}
	
	@Test
	public void getRequestHttpRequestExceptionDataNull() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		GetRequestHttpRequestExceptionnHandler requestHandler = new GetRequestHttpRequestExceptionnHandler(null);
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, false), requestHandler);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).getContentType();
		verify(response, times(1)).getOutputStream();
		verify(response, times(1)).setStatus(StatusCodes.BAD_REQUEST);
		
		componentManager.destroy();
	}
	
	@Test
	public void getRequestUnknownExceptionContentTypeIsNull() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		ExceptionHandlerManager exceptionHandlerManager = componentManager.getExceptionHandlerManager();
        ExExceptionHandler exceptionHandler = new ExExceptionHandler("hello", null);
        exceptionHandlerManager.addUncaughtExceptionHandler(IllegalStateException.class, exceptionHandler);
        
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
        GetRequestUnknownExceptionnHandler requestHandler = new GetRequestUnknownExceptionnHandler(null);
        requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, false), requestHandler);
        
		componentManager.getExceptionHandlerManager();
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).getContentType();
		verify(response, times(1)).getOutputStream();
		verify(response, times(1)).setStatus(StatusCodes.BAD_REQUEST);
		
		componentManager.destroy();
	}
	
	@Test
	public void getRequestUnknownExceptionResultIsNull() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		ExceptionHandlerManager exceptionHandlerManager = componentManager.getExceptionHandlerManager();
        ExExceptionHandler exceptionHandler = new ExExceptionHandler(null, null);
        exceptionHandlerManager.addUncaughtExceptionHandler(IllegalStateException.class, exceptionHandler);
        
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
        GetRequestUnknownExceptionnHandler requestHandler = new GetRequestUnknownExceptionnHandler(null);
        requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, false), requestHandler);
        
		componentManager.getExceptionHandlerManager();
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(2)).setStatus(StatusCodes.BAD_REQUEST);
		
		componentManager.destroy();
	}
	
	@Test
	public void getRequestUnknownExceptionError() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		ExceptionHandlerManager exceptionHandlerManager = componentManager.getExceptionHandlerManager();
        ExExceptionHandler exceptionHandler = new ExExceptionHandler("", null);
        exceptionHandlerManager.addUncaughtExceptionHandler(IllegalStateException.class, exceptionHandler);
        
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
        GetRequestUnknownExceptionnHandler requestHandler = new GetRequestUnknownExceptionnHandler(null);
        requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, false), requestHandler);
        
		componentManager.getExceptionHandlerManager();
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(2)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).setStatus(StatusCodes.BAD_REQUEST);
		verify(response, times(1)).setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
		
		componentManager.destroy();
	}
	
	@Test
	public void getRequestUnknownExceptionNoHandler() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		ExceptionHandlerManager exceptionHandlerManager = componentManager.getExceptionHandlerManager();
		exceptionHandlerManager.destroy();
  
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
        GetRequestUnknownExceptionnHandler requestHandler = new GetRequestUnknownExceptionnHandler(null);
        requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, false), requestHandler);
        
		componentManager.getExceptionHandlerManager();
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(2)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
		
		componentManager.destroy();
	}
	
	@Test
	public void doPostTest() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/post";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.POST.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		when(request.getParameterValues("param")).thenReturn(new String[] {"ParameterValue"});
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] {
		        new Cookie("cookie", "CookieValue"),
		        new Cookie(CoreConstants.COOKIE_REDIRECT_ATTRIBUTES_NAME, "{}")
	        }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		PostRequestHandler requestHandler = new PostRequestHandler();
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.POST, requestURI, false), requestHandler);
		
		RequestInterceptor interceptor = mock(RequestInterceptor.class);
		when(interceptor.preHandle(any(), any())).thenReturn(true);
		componentManager.getInterceptorManager().addRequestInterceptors(Arrays.asList(interceptor));
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).getContentType();
		verify(response, times(1)).getOutputStream();
		verify(response, times(1)).setStatus(StatusCodes.OK);
		
		DataConverters dataConverters = componentManager.getDataConverters();
		BodySerializer bodySerializer = dataConverters.getBodySerializer(ContentTypes.APPLICATION_JSON);
		ExResponse responseData = new ExResponse(
			"Hello ParameterValue, HeaderValue, CookieValue"
		);
		verify(outputStream, times(1)).write(bodySerializer.serialize(responseData));
		
		verify(interceptor, times(1)).preHandle(any(), any());
		verify(interceptor, times(1)).postHandle(any(), any());
		
		componentManager.destroy();
	}
	
	@Test
	public void doPutTest() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/put";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.PUT.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		when(request.getParameterValues("param")).thenReturn(new String[] {"ParameterValue"});
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		PutRequestHandler requestHandler = new PutRequestHandler();
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.PUT, requestURI, false), requestHandler);
		
		RequestInterceptor interceptor = mock(RequestInterceptor.class);
		when(interceptor.preHandle(any(), any())).thenReturn(true);
		componentManager.getInterceptorManager().addRequestInterceptors(Arrays.asList(interceptor));
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).sendRedirect("/home");
		
		verify(interceptor, times(1)).preHandle(any(), any());
		verify(interceptor, times(1)).postHandle(any(), any());
		
		componentManager.destroy();
	}
	
	@Test
    public void doPutWithRedirectTest() throws Exception {
        // given
        ComponentManager componentManager = ComponentManager.getInstance();
        componentManager.addManagementURIs(Collections.emptySet());
        componentManager.setServerPort(PORT);
        
        BlockingServlet sut = new BlockingServlet();
        sut.init();
        
        String requestURI = "/put-with-redirect-attributes";
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.PUT.toString());
        when(request.getRequestURI()).thenReturn(requestURI);
        when(request.getServerPort()).thenReturn(PORT);
        when(request.getParameterNames()).thenReturn(
            Collections.enumeration(Arrays.asList("param"))
        );
        when(request.getParameter("param")).thenReturn("ParameterValue");
        when(request.getParameterValues("param")).thenReturn(new String[] {"ParameterValue"});
        
        when(request.getHeaderNames()).thenReturn(
            Collections.enumeration(Arrays.asList("header"))
        );
        when(request.getHeader("header")).thenReturn("HeaderValue");
        
        when(request.getCookies()).thenReturn(
            new Cookie[] { new Cookie("cookie", "CookieValue") }
        );
        
        RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
        PutWithRedirectAttributesRequestHandler requestHandler =
                new PutWithRedirectAttributesRequestHandler();
        requestHandlerManager.addHandler(new RequestURI(HttpMethod.PUT, requestURI, false), requestHandler);
        
        RequestInterceptor interceptor = mock(RequestInterceptor.class);
        when(interceptor.preHandle(any(), any())).thenReturn(true);
        componentManager.getInterceptorManager().addRequestInterceptors(Arrays.asList(interceptor));
        
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
        
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        
        // when
        sut.service(request, response);
        
        // then
        verify(request, times(1)).getMethod();
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getServerPort();
        
        verify(response, times(1)).sendRedirect("/home");
        
        verify(interceptor, times(1)).preHandle(any(), any());
        verify(interceptor, times(1)).postHandle(any(), any());
        
        componentManager.destroy();
    }
	
	@Test
	public void doDeleteTest() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		ViewContext viewContext = mock(ViewContext.class);
		componentManager.setViewContext(viewContext);
		
		ServletConfig servletConfig = mock(ServletConfig.class);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init(servletConfig);
		sut.init();
		
		String requestURI = "/delete";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.DELETE.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		DeleteRequestHandler requestHandler = new DeleteRequestHandler();
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.DELETE, requestURI, false), requestHandler);
		
		RequestInterceptor interceptor = mock(RequestInterceptor.class);
		when(interceptor.preHandle(any(), any())).thenReturn(true);
		componentManager.getInterceptorManager().addRequestInterceptors(Arrays.asList(interceptor));
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(interceptor, times(1)).preHandle(any(), any());
		verify(interceptor, times(1)).postHandle(any(), any());
		verify(viewContext, times(1)).render(any(), any(), any(), any());
		
		componentManager.destroy();
	}
	
	@Test
	public void doDeleteButViewContextIsNullTest() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		componentManager.setViewContext(null);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/delete";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.DELETE.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		DeleteRequestHandler requestHandler = new DeleteRequestHandler();
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.DELETE, requestURI, false), requestHandler);
		
		RequestInterceptor interceptor = mock(RequestInterceptor.class);
		when(interceptor.preHandle(any(), any())).thenReturn(true);
		componentManager.getInterceptorManager().addRequestInterceptors(Arrays.asList(interceptor));
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(2)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(interceptor, times(1)).preHandle(any(), any());
		verify(interceptor, times(1)).postHandle(any(), any());
		
		componentManager.destroy();
	}
	
	@Test
	public void doGetButResponseBodyIsNullTest() throws Exception {
		// given
		ComponentManager componentManager = ComponentManager.getInstance();
		componentManager.addManagementURIs(Collections.emptySet());
		componentManager.setServerPort(PORT);
		
		BlockingServlet sut = new BlockingServlet();
		sut.init();
		
		String requestURI = "/get-no-body";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getParameterNames()).thenReturn(
			Collections.enumeration(Arrays.asList("param"))
		);
		when(request.getParameter("param")).thenReturn("ParameterValue");
		
		when(request.getHeaderNames()).thenReturn(
			Collections.enumeration(Arrays.asList("header"))
		);
		when(request.getHeader("header")).thenReturn("HeaderValue");
		
		when(request.getCookies()).thenReturn(
			new Cookie[] { new Cookie("cookie", "CookieValue") }
		);
		
		RequestHandlerManager requestHandlerManager = componentManager.getRequestHandlerManager();
		GetNoBodyRequestHandler requestHandler = new GetNoBodyRequestHandler();
		requestHandlerManager.addHandler(new RequestURI(HttpMethod.GET, requestURI, false), requestHandler);
		
		RequestInterceptor interceptor = mock(RequestInterceptor.class);
		when(interceptor.preHandle(any(), any())).thenReturn(true);
		componentManager.getInterceptorManager().addRequestInterceptors(Arrays.asList(interceptor));
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);
		
		// when
		sut.service(request, response);
		
		// then
		verify(request, times(1)).getMethod();
		verify(request, times(1)).getRequestURI();
		verify(request, times(1)).getServerPort();
		
		verify(response, times(1)).setStatus(StatusCodes.OK);
		
		verify(interceptor, times(1)).preHandle(any(), any());
		verify(interceptor, times(1)).postHandle(any(), any());
		
		componentManager.destroy();
	}
	
	@AllArgsConstructor
	public static class ExExceptionHandler implements UncaughtExceptionHandler {

		private final Object data;
		private final String contentType;
		
		@Override
		public Object handleException(RequestArguments arguments, Exception exception) throws Exception {
			arguments.getResponse().setStatus(StatusCodes.BAD_REQUEST);
			if(data == "")
				throw new IllegalArgumentException("data can not be null");
			return data;
		}
		
		@Override
		public String getResponseContentType() {
			return contentType;
		}
	}
	
	@AllArgsConstructor
    public static class GetRequestUnknownExceptionnHandler implements RequestHandler {

        private final String contentType;
        
        public GetRequestUnknownExceptionnHandler() {
            this(ContentTypes.APPLICATION_JSON);
        }
        
        @Override
        public Object handle(RequestArguments arguments) throws Exception {
            throw new IllegalStateException("just test");
        }

        @Override
        public HttpMethod getMethod() {
            return HttpMethod.GET;
        }

        @Override
        public String getRequestURI() {
            return "/get";
        }

        @Override
        public String getResponseContentType() {
            return contentType;
        }
    }
	
	@AllArgsConstructor
	public static class GetRequestHttpRequestExceptionnHandler implements RequestHandler {

		private final Object data;
		private final String contentType;
		
		public GetRequestHttpRequestExceptionnHandler(Object data) {
			this(data, ContentTypes.APPLICATION_JSON);
		}
		
		@Override
		public Object handle(RequestArguments arguments) throws Exception {
			throw new HttpRequestException(StatusCodes.BAD_REQUEST, data);
		}

		@Override
		public HttpMethod getMethod() {
			return HttpMethod.GET;
		}

		@Override
		public String getRequestURI() {
			return "/get";
		}

		@Override
		public String getResponseContentType() {
			return contentType;
		}
	}
	
	public static class GetRequestDeserializeValueExceptionHandler implements RequestHandler {

		@Override
		public Object handle(RequestArguments arguments) throws Exception {
			throw new DeserializeValueException("test", "test", String.class, new Exception("just test"));
		}

		@Override
		public HttpMethod getMethod() {
			return HttpMethod.GET;
		}

		@Override
		public String getRequestURI() {
			return "/get";
		}

		@Override
		public String getResponseContentType() {
			return ContentTypes.APPLICATION_JSON;
		}
	}
	
	public static class GetRequestDataNullHandler implements RequestHandler {

		@Override
		public Object handle(RequestArguments arguments) throws Exception {
			return null;
		}

		@Override
		public HttpMethod getMethod() {
			return HttpMethod.GET;
		}

		@Override
		public String getRequestURI() {
			return "/get";
		}

		@Override
		public String getResponseContentType() {
			return null;
		}
	}
	
	
	public static class GetRequestHandlerContentTypeNull implements RequestHandler {

		@Override
		public Object handle(RequestArguments arguments) throws Exception {
			return ResponseEntity.ASYNC;
		}
		
		@Override
		public boolean isAsync() {
		    return true;
		}

		@Override
		public HttpMethod getMethod() {
			return HttpMethod.GET;
		}

		@Override
		public String getRequestURI() {
			return "/get";
		}

		@Override
		public String getResponseContentType() {
			return null;
		}
	}
	
	public static class GetRequestHandler implements RequestHandler {

		@Override
		public Object handle(RequestArguments arguments) throws Exception {
			return CONTROLLER.doGet(
				arguments.getRequest(),
				arguments.getResponse(),
				arguments.getParameter(0),
				arguments.getHeader(0),
				arguments.getCookieValue(0)
			);
		}

		@Override
		public HttpMethod getMethod() {
			return HttpMethod.GET;
		}

		@Override
		public String getRequestURI() {
			return "/get";
		}

		@Override
		public String getResponseContentType() {
			return ContentTypes.APPLICATION_JSON;
		}
	}
	
	public static class GetNoBodyRequestHandler implements RequestHandler {

		@Override
		public Object handle(RequestArguments arguments) throws Exception {
			return CONTROLLER.doGetNoBody(
				arguments.getRequest(),
				arguments.getResponse(),
				arguments.getParameter(0),
				arguments.getHeader(0),
				arguments.getCookieValue(0)
			);
		}

		@Override
		public HttpMethod getMethod() {
			return HttpMethod.GET;
		}

		@Override
		public String getRequestURI() {
			return "/get-no-body";
		}

		@Override
		public String getResponseContentType() {
			return ContentTypes.APPLICATION_JSON;
		}
	}
	
	public static class PostRequestHandler implements RequestHandler {

		@Override
		public Object handle(RequestArguments arguments) throws Exception {
			return CONTROLLER.doPost(
				arguments.getRequest(),
				arguments.getResponse(),
				arguments.getParameter(0),
				arguments.getHeader(0),
				arguments.getCookieValue(0)
			);
		}

		@Override
		public HttpMethod getMethod() {
			return HttpMethod.POST;
		}

		@Override
		public String getRequestURI() {
			return "/post";
		}

		@Override
		public String getResponseContentType() {
			return ContentTypes.APPLICATION_JSON;
		}
	}
	
	public static class PutRequestHandler implements RequestHandler {

		@Override
		public Object handle(RequestArguments arguments) throws Exception {
			return CONTROLLER.doPut(
				arguments.getRequest(),
				arguments.getResponse(),
				arguments.getParameter(0),
				arguments.getHeader(0),
				arguments.getCookieValue(0)
			);
		}

		@Override
		public HttpMethod getMethod() {
			return HttpMethod.PUT;
		}

		@Override
		public String getRequestURI() {
			return "/put";
		}

		@Override
		public String getResponseContentType() {
			return ContentTypes.APPLICATION_JSON;
		}
	}
	
	public static class PutWithRedirectAttributesRequestHandler implements RequestHandler {

        @Override
        public Object handle(RequestArguments arguments) throws Exception {
            return CONTROLLER.doPutWithRedirectAttributes(
                arguments.getRequest(),
                arguments.getResponse(),
                arguments.getParameter(0),
                arguments.getHeader(0),
                arguments.getCookieValue(0)
            );
        }

        @Override
        public HttpMethod getMethod() {
            return HttpMethod.PUT;
        }

        @Override
        public String getRequestURI() {
            return "/put";
        }

        @Override
        public String getResponseContentType() {
            return ContentTypes.APPLICATION_JSON;
        }
    }
	
	public static class DeleteRequestHandler implements RequestHandler {

		@Override
		public Object handle(RequestArguments arguments) throws Exception {
			return CONTROLLER.doDelete(
				arguments.getRequest(),
				arguments.getResponse(),
				arguments.getParameter(0),
				arguments.getHeader(0),
				arguments.getCookieValue(0)
			);
		}

		@Override
		public HttpMethod getMethod() {
			return HttpMethod.DELETE;
		}

		@Override
		public String getRequestURI() {
			return "/delete";
		}

		@Override
		public String getResponseContentType() {
			return ContentTypes.APPLICATION_JSON;
		}
	}
	
	public static class RequestController {
		
		@DoGet("/get")
		public ExResponse doGet(
				HttpServletRequest request,
				HttpServletResponse response,
				@RequestArgument("param") String param,
				@RequestArgument("header") String header,
				@RequestCookie("cookie") String cookie) {
			return new ExResponse("Hello " + param + ", " + header + ", " + cookie);
		}
		
		@DoGet("/get-no-body")
		public ResponseEntity doGetNoBody(
				HttpServletRequest request,
				HttpServletResponse response,
				@RequestArgument("param") String param,
				@RequestArgument("header") String header,
				@RequestCookie("cookie") String cookie) {
			return ResponseEntity.ok(null);
		}
		
		@DoGet("/post")
		public ResponseEntity doPost(
				HttpServletRequest request,
				HttpServletResponse response,
				@RequestArgument("param") String param,
				@RequestArgument("header") String header,
				@RequestCookie("cookie") String cookie) {
			return ResponseEntity.builder()
					.header("foo", "bar")
					.body(new ExResponse("Hello " + param + ", " + header + ", " + cookie))
					.build();
		}
		
		@DoGet("/put")
		public Redirect doPut(
				HttpServletRequest request,
				HttpServletResponse response,
				@RequestArgument("param") String param,
				@RequestArgument("header") String header,
				@RequestCookie("cookie") String cookie) {
			return Redirect.builder()
					.addCookie("foo", "bar")
					.addHeader("hello", "world")
					.uri("/home")
					.build();
		}
		
		@DoGet("/put-with-redirect-attributes")
        public Redirect doPutWithRedirectAttributes(
                HttpServletRequest request,
                HttpServletResponse response,
                @RequestArgument("param") String param,
                @RequestArgument("header") String header,
                @RequestCookie("cookie") String cookie) {
            return Redirect.builder()
                    .addCookie("foo", "bar")
                    .addHeader("hello", "world")
                    .uri("/home")
                    .addAttribute("error", Collections.singletonMap("hello", "world"))
                    .addAttributes(Collections.singletonMap("foo", "bar"))
                    .build();
        }
		
		@DoGet("/delete")
		public View doDelete(
				HttpServletRequest request,
				HttpServletResponse response,
				@RequestArgument("param") String param,
				@RequestArgument("header") String header,
				@RequestCookie("cookie") String cookie) {
			return View.builder()
					.addCookie("foo", "bar")
					.addHeader("hello", "world")
					.addVariable("one", "1")
					.addVariables(Collections.singletonMap("two", "2"))
					.template("/home")
					.build();
		}
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ExRequest {
		private String who;
	}
	
	@Data
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ExResponse {
		private String message;
	}
}
