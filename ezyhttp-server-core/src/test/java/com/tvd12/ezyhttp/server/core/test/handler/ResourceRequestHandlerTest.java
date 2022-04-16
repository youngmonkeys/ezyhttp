package com.tvd12.ezyhttp.server.core.test.handler;

import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.stream.EzyAnywayInputStreamLoader;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.server.core.handler.ResourceRequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.mockito.Mockito.*;

public class ResourceRequestHandlerTest {
    
	@Test
	public void handleAsyncTest() throws Exception {
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
		
		AsyncContext asyncContext = mock(AsyncContext.class);
		when(arguments.getAsyncContext()).thenReturn(asyncContext);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);
        
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
		verify(arguments, times(1)).getAsyncContext();
		verify(response, times(1)).getOutputStream();
		verify(response, times(1)).setStatus(StatusCodes.OK);
        verify(asyncContext, times(1)).getResponse();
		verify(asyncContext, times(1)).complete();
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
            new EzyAnywayInputStreamLoader(),
            downloadManager
        );
        
        RequestArguments arguments = mock(RequestArguments.class);

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(arguments.getAsyncContext()).thenReturn(asyncContext);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);
        
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
        verify(arguments, times(1)).getAsyncContext();
        verify(response, times(1)).getOutputStream();
        verify(response, times(1)).setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
        verify(asyncContext, times(1)).getResponse();
        verify(asyncContext, times(1)).complete();
    }
	
	@SuppressWarnings("unchecked")
    @Test
    public void handleWithDrainExceptionWhenCallTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        ResourceDownloadManager downloadManager = mock(ResourceDownloadManager.class);
        doThrow(IllegalStateException.class).when(downloadManager)
            .drainAsync(
                any(InputStream.class),
                any(OutputStream.class),
                any(EzyResultCallback.class)
            );
        int timeout = RandomUtil.randomSmallInt() + 1;
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            downloadManager,
            timeout
        );
        
        RequestArguments arguments = mock(RequestArguments.class);

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(arguments.getAsyncContext()).thenReturn(asyncContext);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);
        
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        doThrow(IOException.class).when(outputStream).write(any(byte[].class), anyInt(), anyInt());
        
        when(asyncContext.getResponse()).thenReturn(response);
        
        // when
        sut.handle(arguments);
        
        // then
        verify(arguments, times(1)).getAsyncContext();
        verify(response, times(1)).getOutputStream();
        verify(response, times(1)).setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
        verify(asyncContext, times(1)).setTimeout(timeout);
        verify(asyncContext, times(1)).getResponse();
        verify(asyncContext, times(1)).complete();
    }
}
