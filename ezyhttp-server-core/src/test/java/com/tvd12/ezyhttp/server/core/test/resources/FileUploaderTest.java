package com.tvd12.ezyhttp.server.core.test.resources;

import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.function.EzyExceptionVoid;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.MaxUploadSizeException;
import com.tvd12.ezyhttp.core.resources.ResourceUploadManager;
import com.tvd12.ezyhttp.server.core.resources.FileUploadCallback;
import com.tvd12.ezyhttp.server.core.resources.FileUploader;
import org.testng.annotations.Test;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FileUploaderTest {

    @SuppressWarnings("unchecked")
    @Test
    public void acceptFirst() throws Exception {
        // given
        AsyncContext asyncContext = mock(AsyncContext.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);
        
        Part part = mock(Part.class);
        File outputFile = new File("test-output/files");
        EzyExceptionVoid callback = mock(EzyExceptionVoid.class);
        
        ResourceUploadManager resourceUploadManager = mock(ResourceUploadManager.class);
        doAnswer(it -> {
            EzyResultCallback<Boolean> cb = it.getArgumentAt(3, EzyResultCallback.class);
            cb.onResponse(Boolean.TRUE);
            return null;
        }).when(resourceUploadManager).drainAsync(any(), any(), any(long.class), any());
        
        FileUploader sut = new FileUploader(resourceUploadManager);
        
        // when
        sut.accept(asyncContext, part, outputFile, callback);
        
        // then
        verify(callback, times(1)).apply();
        verify(resourceUploadManager, times(1)).drainAsync(any(), any(), any(long.class), any());
        verify(response, times(1)).setStatus(StatusCodes.OK);
        verify(asyncContext, times(1)).complete();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void acceptFirstException() throws Exception {
        // given
        AsyncContext asyncContext = mock(AsyncContext.class);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("hello-world");
        when(asyncContext.getRequest()).thenReturn(request);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);
        
        Part part = mock(Part.class);
        File outputFile = new File("test-output/files");
        EzyExceptionVoid callback = mock(EzyExceptionVoid.class);
        doThrow(IllegalStateException.class).when(callback).apply();
        
        ResourceUploadManager resourceUploadManager = mock(ResourceUploadManager.class);
        doAnswer(it -> {
            EzyResultCallback<Boolean> cb = it.getArgumentAt(3, EzyResultCallback.class);
            cb.onResponse(Boolean.TRUE);
            return null;
        }).when(resourceUploadManager).drainAsync(any(), any(), any(long.class), any());
        
        FileUploader sut = new FileUploader(resourceUploadManager);
        
        // when
        sut.accept(asyncContext, part, outputFile, callback);
        
        // then
        verify(callback, times(1)).apply();
        verify(resourceUploadManager, times(1)).drainAsync(any(), any(), any(long.class), any());
        verify(response, times(1)).setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
        verify(request, times(1)).getRequestURI();
        verify(asyncContext, times(1)).getRequest();
        verify(asyncContext, times(1)).complete();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void acceptFirstMaxUploadSizeException() throws Exception {
        // given
        AsyncContext asyncContext = mock(AsyncContext.class);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("hello-world");
        when(asyncContext.getRequest()).thenReturn(request);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);
        
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        
        Part part = mock(Part.class);
        File outputFile = new File("test-output/files");
        EzyExceptionVoid callback = mock(EzyExceptionVoid.class);
        doThrow(new MaxUploadSizeException(100)).when(callback).apply();
        
        ResourceUploadManager resourceUploadManager = mock(ResourceUploadManager.class);
        doAnswer(it -> {
            EzyResultCallback<Boolean> cb = it.getArgumentAt(3, EzyResultCallback.class);
            cb.onResponse(Boolean.TRUE);
            return null;
        }).when(resourceUploadManager).drainAsync(any(), any(), any(long.class), any());
        
        FileUploader sut = new FileUploader(resourceUploadManager);
        
        // when
        sut.accept(asyncContext, part, outputFile, callback);
        
        // then
        verify(callback, times(1)).apply();
        verify(resourceUploadManager, times(1)).drainAsync(any(), any(), any(long.class), any());
        verify(response, times(1)).setStatus(StatusCodes.BAD_REQUEST);
        verify(response, times(1)).getOutputStream();
        verify(outputStream, times(1)).write(any(byte[].class));
        verify(request, times(1)).getRequestURI();
        verify(asyncContext, times(1)).getRequest();
        verify(asyncContext, times(1)).complete();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void acceptFirstFailed() throws Exception {
        // given
        AsyncContext asyncContext = mock(AsyncContext.class);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("hello-world");
        when(asyncContext.getRequest()).thenReturn(request);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(asyncContext.getResponse()).thenReturn(response);

        Part part = mock(Part.class);
        File outputFile = new File("test-output/files");
        EzyExceptionVoid callback = mock(EzyExceptionVoid.class);
        
        ResourceUploadManager resourceUploadManager = mock(ResourceUploadManager.class);
        doAnswer(it -> {
            EzyResultCallback<Boolean> cb = it.getArgumentAt(3, EzyResultCallback.class);
            cb.onException(new Exception("just test"));
            return null;
        }).when(resourceUploadManager).drainAsync(any(), any(), any(long.class), any());
        
        FileUploader sut = new FileUploader(resourceUploadManager);
        
        // when
        sut.accept(asyncContext, part, outputFile, callback);
        
        // then
        verify(callback, times(0)).apply();
        verify(resourceUploadManager, times(1)).drainAsync(any(), any(), any(long.class), any());
        verify(response, times(1)).setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
        verify(request, times(1)).getRequestURI();
        verify(asyncContext, times(1)).getRequest();
        verify(asyncContext, times(1)).complete();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void acceptSecondFailed() throws IOException {
        // given
        AsyncContext asyncContext = mock(AsyncContext.class);
        Part part = mock(Part.class);
        when(part.getInputStream()).thenThrow(IOException.class);
        File outputFile = new File("test-output/files");
        FileUploadCallback callback = mock(FileUploadCallback.class);
        
        ResourceUploadManager resourceUploadManager = mock(ResourceUploadManager.class);
        
        FileUploader sut = new FileUploader(resourceUploadManager);
        
        // when
        sut.accept(asyncContext, part, outputFile, callback);
        
        // then
        verify(callback, times(1)).onFailure(any());
        verify(asyncContext, times(1)).complete();
    }
    
    @Test
    public void acceptThirdFileNotFound() {
        // given
        HttpServletResponse response = mock(HttpServletResponse.class);
        AsyncContext asyncContext = mock(AsyncContext.class);
        when(asyncContext.getResponse()).thenReturn(response);
        InputStream inputStream = mock(InputStream.class);
        File outputFile = new File("");
        FileUploadCallback callback = mock(FileUploadCallback.class);
        
        ResourceUploadManager resourceUploadManager = mock(ResourceUploadManager.class);
        
        FileUploader sut = new FileUploader(resourceUploadManager);
        
        // when
        sut.accept(asyncContext, inputStream, outputFile, callback);
        
        // then
        verify(callback, times(1)).onFailure(any());
        verify(asyncContext, times(1)).complete();
    }
    
    @Test
    public void acceptFourthFailed() {
        // given
        HttpServletResponse response = mock(HttpServletResponse.class);
        AsyncContext asyncContext = mock(AsyncContext.class);
        when(asyncContext.getResponse()).thenReturn(response);
        InputStream inputStream = mock(InputStream.class);
        OutputStream outputStream = mock(OutputStream.class);
        FileUploadCallback callback = mock(FileUploadCallback.class);
        
        ResourceUploadManager resourceUploadManager = mock(ResourceUploadManager.class);
        doThrow(IllegalStateException.class).when(resourceUploadManager)
            .drainAsync(
                any(),
                any(),
                any(long.class),
                any()
            );

        FileUploader sut = new FileUploader(resourceUploadManager);
        
        // when
        sut.accept(asyncContext, inputStream, outputStream, callback);
        
        // then
        verify(callback, times(1)).onFailure(any());
        verify(resourceUploadManager, times(1)).drainAsync(any(), any(), any(long.class), any());
        verify(asyncContext, times(1)).complete();
    }
}
