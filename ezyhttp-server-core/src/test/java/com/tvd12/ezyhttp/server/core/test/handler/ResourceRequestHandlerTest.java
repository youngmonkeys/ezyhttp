package com.tvd12.ezyhttp.server.core.test.handler;

import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.exception.EzyFileNotFoundException;
import com.tvd12.ezyfox.stream.EzyAnywayInputStreamLoader;
import com.tvd12.ezyfox.stream.EzyInputStreamLoader;
import com.tvd12.ezyhttp.core.constant.*;
import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import com.tvd12.ezyhttp.core.io.AnywayFileLoader;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.handler.ResourceRequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

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

    @SuppressWarnings("unchecked")
    @Test
    public void handleAsyncWitCallbackTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        ResourceDownloadManager downloadManager = new ResourceDownloadManager();
        EzyInputStreamLoader inputStreamLoader = mock(EzyInputStreamLoader.class);
        InputStream inputStream = mock(InputStream.class);
        when(inputStreamLoader.load(resourcePath)).thenReturn(inputStream);
        EzyResultCallback<Boolean> callback = mock(EzyResultCallback.class);
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            inputStreamLoader,
            downloadManager,
            0,
            callback
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
        verify(inputStreamLoader, times(1)).load(resourcePath);
        verify(inputStream, times(1)).close();
        verify(callback, times(1)).onResponse(true);
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
    public void handleWithDrainExceptionWitCallbackTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        ResourceDownloadManager downloadManager = new ResourceDownloadManager();
        EzyInputStreamLoader inputStreamLoader = mock(EzyInputStreamLoader.class);
        InputStream inputStream = mock(InputStream.class);
        when(inputStream.read(any())).thenReturn(1);
        when(inputStreamLoader.load(resourcePath)).thenReturn(inputStream);
        EzyResultCallback<Boolean> callback = mock(EzyResultCallback.class);
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            inputStreamLoader,
            downloadManager,
            0,
            callback
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
        verify(arguments, times(1)).getMethod();
        verify(arguments, times(1)).getRequest();
        verify(response, times(1)).getOutputStream();
        verify(arguments, times(1)).getHeader("Range");
        verify(response, times(1)).setHeader(Headers.ACCEPT_RANGES, "bytes");
        verify(response, times(1)).setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
        verify(response, times(1)).setContentType("text/html; charset=utf-8");
        verify(asyncContext, times(1)).getResponse();
        verify(asyncContext, times(1)).complete();
        verify(inputStreamLoader, times(1)).load(resourcePath);
        verify(inputStream, times(1)).read(any());
        verify(inputStream, times(1)).close();
        verify(callback, times(1)).onException(any(IOException.class));

        verifyNoMoreInteractions(
            arguments,
            response,
            asyncContext,
            inputStreamLoader,
            inputStream,
            callback
        );
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

    @Test
    public void handleHeadRequestTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        ResourceDownloadManager downloadManager = mock(ResourceDownloadManager.class);
        EzyResultCallback<Boolean> callback = mock(EzyResultCallback.class);
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            new EzyAnywayInputStreamLoader(),
            downloadManager,
            0,
            callback
        );

        RequestArguments arguments = mock(RequestArguments.class);
        when(arguments.getMethod()).thenReturn(HttpMethod.HEAD);

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(arguments.getAsyncContext()).thenReturn(asyncContext);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);

        long fileLength = AnywayFileLoader
            .getDefault()
            .load(resourcePath)
            .length();

        // when
        Object actual = sut.handle(arguments);

        // then
        Asserts.assertEquals(ResponseEntity.ASYNC, actual);
        verify(response, times(1)).setHeader(Headers.ACCEPT_RANGES, "bytes");
        verify(response, times(1)).setContentLengthLong(fileLength);
        verify(response, times(1)).setStatus(StatusCodes.OK);
        verify(response, times(0)).getOutputStream();
        verify(asyncContext, times(1)).complete();
        verify(arguments, times(0)).getHeader("Range");
        verifyNoMoreInteractions(downloadManager, callback);
    }

    @Test
    public void handleHeadRequestByServletMethodTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        ResourceDownloadManager downloadManager = mock(ResourceDownloadManager.class);
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            downloadManager
        );

        RequestArguments arguments = mock(RequestArguments.class);
        when(arguments.getMethod()).thenReturn(HttpMethod.GET);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("HEAD");
        when(arguments.getRequest()).thenReturn(request);

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(arguments.getAsyncContext()).thenReturn(asyncContext);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);

        // when
        sut.handle(arguments);

        // then
        verify(response, times(1)).setStatus(StatusCodes.OK);
        verify(response, times(0)).getOutputStream();
        verify(asyncContext, times(1)).complete();
        verifyNoMoreInteractions(downloadManager);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void handleHeadRequestWithCustomLoaderTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        ResourceDownloadManager downloadManager = mock(ResourceDownloadManager.class);
        EzyInputStreamLoader inputStreamLoader = mock(EzyInputStreamLoader.class);
        InputStream inputStream = mock(InputStream.class);
        when(inputStreamLoader.load(resourcePath)).thenReturn(inputStream);
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            inputStreamLoader,
            downloadManager
        );

        RequestArguments arguments = mock(RequestArguments.class);
        when(arguments.getMethod()).thenReturn(HttpMethod.HEAD);

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(arguments.getAsyncContext()).thenReturn(asyncContext);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);

        // when
        sut.handle(arguments);

        // then
        verify(inputStreamLoader, times(1)).load(resourcePath);
        verify(inputStream, times(1)).close();
        verify(inputStream, times(0)).read(any());
        verify(response, times(0)).setContentLengthLong(anyLong());
        verify(response, times(1)).setStatus(StatusCodes.OK);
        verify(asyncContext, times(1)).complete();
        verifyNoMoreInteractions(downloadManager);
    }

    @Test
    public void handleHeadRequestWithCustomFileLoaderTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        File file = AnywayFileLoader
            .getDefault()
            .load(resourcePath);
        ResourceDownloadManager downloadManager = mock(ResourceDownloadManager.class);
        EzyInputStreamLoader inputStreamLoader = path -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
        };
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            inputStreamLoader,
            downloadManager
        );

        RequestArguments arguments = mock(RequestArguments.class);
        when(arguments.getMethod()).thenReturn(HttpMethod.HEAD);

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(arguments.getAsyncContext()).thenReturn(asyncContext);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);

        // when
        sut.handle(arguments);

        // then
        verify(response, times(1)).setContentLengthLong(file.length());
        verify(response, times(1)).setStatus(StatusCodes.OK);
        verify(response, times(0)).getOutputStream();
        verify(asyncContext, times(1)).complete();
        verifyNoMoreInteractions(downloadManager);
    }

    @Test
    public void handleHeadRequestNotFoundTest() throws Exception {
        // given
        String resourcePath = "not found.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        ResourceDownloadManager downloadManager = mock(ResourceDownloadManager.class);
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            downloadManager
        );

        RequestArguments arguments = mock(RequestArguments.class);
        when(arguments.getMethod()).thenReturn(HttpMethod.HEAD);

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(arguments.getAsyncContext()).thenReturn(asyncContext);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);

        // when
        Throwable e = Asserts.assertThrows(() -> sut.handle(arguments));

        // then
        Asserts.assertEqualsType(e, HttpNotFoundException.class);
        verify(response, times(0)).setHeader(Headers.ACCEPT_RANGES, "bytes");
        verify(response, times(0)).setStatus(StatusCodes.OK);
        verify(asyncContext, times(1)).complete();
        verifyNoMoreInteractions(downloadManager);
    }

    @Test
    public void handleGetWithoutRangeSetContentLengthTest() throws Exception {
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
        when(arguments.getMethod()).thenReturn(HttpMethod.GET);

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(arguments.getAsyncContext()).thenReturn(asyncContext);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);

        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        long fileLength = AnywayFileLoader
            .getDefault()
            .load(resourcePath)
            .length();

        // when
        sut.handle(arguments);

        // then
        Thread.sleep(300);
        downloadManager.stop();
        verify(response, times(1)).setHeader(Headers.ACCEPT_RANGES, "bytes");
        verify(response, times(1)).setContentLengthLong(fileLength);
        verify(response, times(1)).getOutputStream();
        verify(asyncContext, times(1)).complete();
    }

    @Test
    public void handleGetWithoutRangeSetContentLengthErrorTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        File file = AnywayFileLoader
            .getDefault()
            .load(resourcePath);
        AtomicBoolean closed = new AtomicBoolean();
        ResourceDownloadManager downloadManager = mock(ResourceDownloadManager.class);
        EzyInputStreamLoader inputStreamLoader = path -> {
            try {
                return new FileInputStream(file) {
                    @Override
                    public void close() throws IOException {
                        closed.set(true);
                        super.close();
                    }
                };
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
        };
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            inputStreamLoader,
            downloadManager
        );

        RequestArguments arguments = mock(RequestArguments.class);
        when(arguments.getMethod()).thenReturn(HttpMethod.GET);

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(arguments.getAsyncContext()).thenReturn(asyncContext);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);
        RuntimeException exception = new RuntimeException("test");
        doThrow(exception).when(response).setContentLengthLong(anyLong());

        // when
        Throwable e = Asserts.assertThrows(() -> sut.handle(arguments));

        // then
        Asserts.assertEquals(e, exception);
        Asserts.assertTrue(closed.get());
        verify(response, times(1)).setContentLengthLong(file.length());
        verify(response, times(0)).getOutputStream();
        verify(asyncContext, times(1)).complete();
        verifyNoMoreInteractions(downloadManager);
    }

    @Test
    public void handleAsyncWithRangeExceptionAfterOpenFileTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        ResourceDownloadManager downloadManager = mock(ResourceDownloadManager.class);
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            downloadManager
        );

        RequestArguments arguments = mock(RequestArguments.class);
        when(arguments.getMethod()).thenReturn(HttpMethod.GET);
        when(arguments.getHeader("Range")).thenReturn("bytes=0-10");

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(arguments.getAsyncContext()).thenReturn(asyncContext);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);
        RuntimeException exception = new RuntimeException("test");
        doThrow(exception)
            .when(response)
            .setHeader(eq(Headers.CONTENT_RANGE), anyString());

        // when
        Throwable e = Asserts.assertThrows(() -> sut.handle(arguments));

        // then
        Asserts.assertEquals(e, exception);
        verify(response, times(1)).setHeader(Headers.ACCEPT_RANGES, "bytes");
        verify(response, times(1))
            .setHeader(eq(Headers.CONTENT_RANGE), anyString());
        verify(response, times(0))
            .setHeader(eq(Headers.CONTENT_LENGTH), anyString());
        verify(response, times(0)).getOutputStream();
        verify(asyncContext, times(1)).complete();
        verifyNoMoreInteractions(downloadManager);
    }

    @Test
    public void handleGetRequestByServletMethodTest() throws Exception {
        // given
        String resourcePath = "static/index.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        ResourceDownloadManager downloadManager = mock(ResourceDownloadManager.class);
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            downloadManager
        );

        RequestArguments arguments = mock(RequestArguments.class);
        when(arguments.getMethod()).thenReturn(HttpMethod.GET);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(arguments.getRequest()).thenReturn(request);

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(arguments.getAsyncContext()).thenReturn(asyncContext);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);

        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        // when
        sut.handle(arguments);

        // then
        verify(request, times(1)).getMethod();
        verify(arguments, times(1)).getHeader("Range");
        verify(response, times(1)).getOutputStream();
        verify(downloadManager, times(1)).drainAsync(
            any(InputStream.class),
            eq(outputStream),
            any()
        );
        verify(asyncContext, times(0)).complete();
    }

    @Test
    public void handleHeadRequestWithCustomLoaderAndNoFileTest() throws Exception {
        // given
        String resourcePath = "not-exist-resource-file.html";
        String resourceURI = "/index.html";
        String resourceExtension = "html";
        ResourceDownloadManager downloadManager = mock(ResourceDownloadManager.class);
        EzyInputStreamLoader inputStreamLoader = mock(EzyInputStreamLoader.class);
        InputStream inputStream = mock(InputStream.class);
        when(inputStreamLoader.load(resourcePath)).thenReturn(inputStream);
        ResourceRequestHandler sut = new ResourceRequestHandler(
            resourcePath,
            resourceURI,
            resourceExtension,
            inputStreamLoader,
            downloadManager
        );

        RequestArguments arguments = mock(RequestArguments.class);
        when(arguments.getMethod()).thenReturn(HttpMethod.HEAD);

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(arguments.getAsyncContext()).thenReturn(asyncContext);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);

        // when
        sut.handle(arguments);

        // then
        verify(inputStreamLoader, times(1)).load(resourcePath);
        verify(inputStream, times(1)).close();
        verify(response, times(0)).setHeader(Headers.ACCEPT_RANGES, "bytes");
        verify(response, times(0)).setContentLengthLong(anyLong());
        verify(response, times(1)).setStatus(StatusCodes.OK);
        verify(asyncContext, times(1)).complete();
        verifyNoMoreInteractions(downloadManager);
    }
}
