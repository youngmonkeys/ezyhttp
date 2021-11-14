package com.tvd12.ezyhttp.server.core.test.resources;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.function.EzyExceptionVoid;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.server.core.exception.MaxUploadSizeException;
import com.tvd12.ezyhttp.server.core.resources.FileUploadCallback;
import com.tvd12.ezyhttp.server.core.resources.FileUploader;
import com.tvd12.ezyhttp.server.core.resources.ResourceUploadManager;

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
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void acceptFirstException() throws Exception {
        // given
        AsyncContext asyncContext = mock(AsyncContext.class);
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
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void acceptFirstMaxUploadSizeException() throws Exception {
        // given
        AsyncContext asyncContext = mock(AsyncContext.class);
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
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void acceptFirstFailed() throws Exception {
        // given
        HttpServletResponse response = mock(HttpServletResponse.class);
        AsyncContext asyncContext = mock(AsyncContext.class);
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
    }
    
    @SuppressWarnings("unchecked")
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
        when(resourceUploadManager.drainAsync(any(), any(), any(long.class), any())).thenThrow(IllegalStateException.class);
        
        FileUploader sut = new FileUploader(resourceUploadManager);
        
        // when
        sut.accept(asyncContext, inputStream, outputStream, callback);
        
        // then
        verify(callback, times(1)).onFailure(any());
        verify(resourceUploadManager, times(1)).drainAsync(any(), any(), any(long.class), any());
    }
}
