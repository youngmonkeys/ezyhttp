package com.tvd12.ezyhttp.server.core.test.handler;

import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.exception.EzyFileNotFoundException;
import com.tvd12.ezyfox.stream.EzyAnywayInputStreamLoader;
import com.tvd12.ezyfox.stream.EzyInputStreamLoader;
import com.tvd12.ezyhttp.core.constant.*;
import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.server.core.handler.ResourceRequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
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
    public void handleAsyncWithRangeTest() throws Exception {
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

        String range = "bytes=0-" + new File(resourcePath).length();
        when(arguments.getHeader("Range")).thenReturn(range);

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
        verify(response, times(2)).setStatus(StatusCodes.PARTIAL_CONTENT);
        verify(asyncContext, times(1)).getResponse();
        verify(asyncContext, times(1)).complete();
    }

    @Test
    public void handleAsyncFileNotFoundTest() throws Exception {
        // given
        String resourcePath = "file not found";
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
        Throwable e = Asserts.assertThrows(() -> sut.handle(arguments));

        // then
        Asserts.assertEqualsType(e, HttpNotFoundException.class);
        Asserts.assertTrue(sut.isAsync());
        Asserts.assertEquals(HttpMethod.GET, sut.getMethod());
        Asserts.assertEquals("/index.html", sut.getRequestURI());
        Asserts.assertEquals(ContentTypes.TEXT_HTML_UTF8, sut.getResponseContentType());
        Thread.sleep(300);
        downloadManager.stop();
        verify(arguments, times(1)).getAsyncContext();
        verify(asyncContext, times(1)).complete();
    }

    @Test
    public void handleAsyncEzyFileNotFoundTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";

        EzyInputStreamLoader inputStreamLoader = mock(EzyAnywayInputStreamLoader.class);
        EzyFileNotFoundException exception = new EzyFileNotFoundException("just test");
        when(inputStreamLoader.load(resourcePath)).thenThrow(exception);

        ResourceDownloadManager downloadManager = new ResourceDownloadManager();
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            inputStreamLoader,
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
        Throwable e = Asserts.assertThrows(() -> sut.handle(arguments));

        // then
        Asserts.assertEqualsType(e, HttpNotFoundException.class);
        Asserts.assertTrue(sut.isAsync());
        Asserts.assertEquals(HttpMethod.GET, sut.getMethod());
        Asserts.assertEquals("/index.html", sut.getRequestURI());
        Asserts.assertEquals(ContentTypes.TEXT_HTML_UTF8, sut.getResponseContentType());
        Thread.sleep(300);
        downloadManager.stop();
        verify(arguments, times(1)).getAsyncContext();
        verify(asyncContext, times(1)).complete();
    }

    @Test
    public void handleAsyncRunTimeExceptionTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";

        EzyInputStreamLoader inputStreamLoader = mock(EzyAnywayInputStreamLoader.class);
        RuntimeException exception = new RuntimeException("just test");
        when(inputStreamLoader.load(resourcePath)).thenThrow(exception);

        ResourceDownloadManager downloadManager = new ResourceDownloadManager();
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            inputStreamLoader,
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
        Throwable e = Asserts.assertThrows(() -> sut.handle(arguments));

        // then
        Asserts.assertEqualsType(e, RuntimeException.class);
        Asserts.assertTrue(sut.isAsync());
        Asserts.assertEquals(HttpMethod.GET, sut.getMethod());
        Asserts.assertEquals("/index.html", sut.getRequestURI());
        Asserts.assertEquals(ContentTypes.TEXT_HTML_UTF8, sut.getResponseContentType());
        Thread.sleep(300);
        downloadManager.stop();
        verify(arguments, times(1)).getAsyncContext();
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

    @Test
    public void handleResourceWithTwoPartsExtension() throws Exception {
        // given
        String resourcePath = "static/wasm/resource.wasm.gz";
        String resourceURI = "/wasm/resource.wasm.gz";
        String resourceExtension = "gz";
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

        // when
        sut.handle(arguments);

        // then
        verify(response, times(1))
            .setHeader(Headers.CONTENT_ENCODING, ContentEncoding.GZIP.getValue());
        verify(response, times(1))
            .setContentType(ContentTypes.APPLICATION_WASM);
    }

    @Test
    public void handleAsyncWithRangeExceptionWhenServletResponseSetHeaderTest() throws Exception {
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
        doThrow(new RuntimeException("test"))
            .when(response)
            .setHeader(Headers.ACCEPT_RANGES, "bytes");
        when(asyncContext.getResponse()).thenReturn(response);

        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        when(asyncContext.getResponse()).thenReturn(response);

        String range = "bytes=0-" + new File(resourcePath).length();
        when(arguments.getHeader("Range")).thenReturn(range);

        // when
        Throwable e = Asserts.assertThrows(() -> sut.handle(arguments));

        // then
        Asserts.assertEqualsType(e, RuntimeException.class);
        Asserts.assertTrue(sut.isAsync());
        Asserts.assertEquals(HttpMethod.GET, sut.getMethod());
        Asserts.assertEquals("/index.html", sut.getRequestURI());
        Asserts.assertEquals(ContentTypes.TEXT_HTML_UTF8, sut.getResponseContentType());
        Thread.sleep(300);
        downloadManager.stop();
        verify(arguments, times(1)).getAsyncContext();
        verify(asyncContext, times(1)).getResponse();
        verify(asyncContext, times(1)).complete();
    }

    @Test
    public void handleAsyncWithRangeExceptionTest() throws Exception {
        // given
        String resourcePath = "not found.html";
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
        doThrow(new RuntimeException("test"))
            .when(response)
            .setHeader(Headers.ACCEPT_RANGES, "bytes");
        when(asyncContext.getResponse()).thenReturn(response);

        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        when(asyncContext.getResponse()).thenReturn(response);

        String range = "bytes=0-" + new File(resourcePath).length();
        when(arguments.getHeader("Range")).thenReturn(range);

        // when
        Throwable e = Asserts.assertThrows(() -> sut.handle(arguments));

        // then
        Asserts.assertEqualsType(e, HttpNotFoundException.class);
        Asserts.assertTrue(sut.isAsync());
        Asserts.assertEquals(HttpMethod.GET, sut.getMethod());
        Asserts.assertEquals("/index.html", sut.getRequestURI());
        Asserts.assertEquals(ContentTypes.TEXT_HTML_UTF8, sut.getResponseContentType());
        Thread.sleep(300);
        downloadManager.stop();
        verify(arguments, times(1)).getAsyncContext();
        verify(asyncContext, times(1)).getResponse();
        verify(asyncContext, times(1)).complete();
    }
}
