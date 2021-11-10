package com.tvd12.ezyhttp.server.core.test.handler;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.server.core.handler.ResourceRequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.resources.ResourceDownloadManager;
import com.tvd12.test.assertion.Asserts;

public class ResourceRequestHandlerTest {
    
	@Test
	public void handleAsynctest() throws Exception {
		// given
		String resourcePath = "static/index.html";
	    String resourceURI = "/index.html";
	    String resourceExtension = "html";
	    ResourceDownloadManager downloadManager = new ResourceDownloadManager();
		ResourceRequestHandler sut = new ResourceRequestHandler(
			resourcePath,
			resourceURI,
			resourceExtension,
			downloadManager
		);
		
		RequestArguments arguments = mock(RequestArguments.class);
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(arguments.getRequest()).thenReturn(request);
		
		AsyncContext asyncContext = mock(AsyncContext.class);
		when(request.getAsyncContext()).thenReturn(asyncContext);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
        when(arguments.getResponse()).thenReturn(response);
        
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        
        when(asyncContext.getResponse()).thenReturn(response);
		
		// when
		sut.handle(arguments);
		
		// then
		Asserts.assertTrue(sut.isAsync());
		Asserts.assertEquals(HttpMethod.GET, sut.getMethod());
		Asserts.assertEquals("/index.html", sut.getRequestURI());
		Asserts.assertEquals(ContentTypes.TEXT_HTML_UTF8, sut.getResponseContentType());
		Thread.sleep(300);
		downloadManager.stop();
		verify(response, times(1)).setStatus(StatusCodes.OK);
	}
	
	@Test
    public void handleWithDrainExceptionTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        ResourceDownloadManager downloadManager = new ResourceDownloadManager();
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            downloadManager
        );
        
        RequestArguments arguments = mock(RequestArguments.class);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(arguments.getRequest()).thenReturn(request);
        
        AsyncContext asyncContext = mock(AsyncContext.class);
        when(request.getAsyncContext()).thenReturn(asyncContext);
        
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(arguments.getResponse()).thenReturn(response);
        
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        doThrow(IOException.class).when(outputStream).write(any(byte[].class), anyInt(), anyInt());
        
        when(asyncContext.getResponse()).thenReturn(response);
        
        // when
        sut.handle(arguments);
        
        // then
        Asserts.assertEquals(HttpMethod.GET, sut.getMethod());
        Asserts.assertEquals("/index.html", sut.getRequestURI());
        Asserts.assertEquals(ContentTypes.TEXT_HTML_UTF8, sut.getResponseContentType());
        Thread.sleep(300);
        downloadManager.stop();
        verify(response, times(1)).setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
    }
	
	@SuppressWarnings("unchecked")
    @Test
    public void handleWithDrainExceptionWhenCallTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        ResourceDownloadManager downloadManager = mock(ResourceDownloadManager.class);
        when(
            downloadManager.drainAsync(
                any(InputStream.class), 
                any(OutputStream.class),
                any(EzyResultCallback.class)
            )
        ).thenThrow(IllegalStateException.class);
        
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            downloadManager
        );
        
        RequestArguments arguments = mock(RequestArguments.class);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(arguments.getRequest()).thenReturn(request);
        
        AsyncContext asyncContext = mock(AsyncContext.class);
        when(request.getAsyncContext()).thenReturn(asyncContext);
        
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(arguments.getResponse()).thenReturn(response);
        
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        doThrow(IOException.class).when(outputStream).write(any(byte[].class), anyInt(), anyInt());
        
        when(asyncContext.getResponse()).thenReturn(response);
        
        // when
        sut.handle(arguments);
        
        // then
        verify(response, times(1)).setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
    }
	
}
