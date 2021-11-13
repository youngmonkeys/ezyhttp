package com.tvd12.ezyhttp.server.core.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.function.EzyExceptionVoid;
import com.tvd12.ezyfox.util.EzyFileUtil;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.EzyProcessor;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.server.core.exception.MaxUploadSizeException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileUploader extends EzyLoggable {

    private final ResourceUploadManager resourceUploadManager;
    
    private static final byte[] OVER_UPLOAD_SIZE_MESSAGE = 
            "{\"uploadSize\":\"over\"}".getBytes();
    
    public void accept(
        AsyncContext asyncContext,
        Part part,
        File outputFile,
        EzyExceptionVoid callback
    ) {
        HttpServletResponse response = 
                (HttpServletResponse)asyncContext.getResponse();
        accept(asyncContext, part, outputFile, new FileUploadCallback() {
            @Override
            public void onSuccess() {
                try {
                    callback.apply();
                    response.setStatus(StatusCodes.OK);
                }
                catch (Exception e) {
                    onFailure(e);
                }
            }
            
            @Override
            public void onFailure(Exception e) {
                if (e instanceof MaxUploadSizeException) {
                    EzyProcessor.processWithLogException(() ->
                        response.getOutputStream().write(OVER_UPLOAD_SIZE_MESSAGE)
                    );
                    response.setStatus(StatusCodes.BAD_REQUEST);
                } else {
                    response.setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
                }
                logger.warn("FileUploader.accept request: {} error", asyncContext.getRequest(), e);
            }
        });
    }
    
    public void accept(
        AsyncContext asyncContext,
        Part part,
        File outputFile,
        FileUploadCallback callback
    ) {
        try {
            accept(asyncContext, part.getInputStream(), outputFile, callback);
        }
        catch (Exception e) {
            callback.onFailure(e);
        }
    }
    
    public void accept(
        AsyncContext asyncContext,
        InputStream inputStream,
        File outputFile,
        FileUploadCallback callback
    ) {
        try {
            EzyFileUtil.createFileIfNotExists(outputFile);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            accept(asyncContext, inputStream, outputStream, new FileUploadCallback() {
                @Override
                public void onSuccess() {
                    EzyProcessor.processWithLogException(outputStream::close);
                    callback.onSuccess();
                }
                
                @Override
                public void onFailure(Exception e) {
                    EzyProcessor.processWithLogException(outputStream::close);
                    callback.onFailure(e);
                }
            });
        }
        catch (Exception e) {
            callback.onFailure(e);
        }
    }
    
    public void accept(
        AsyncContext asyncContext,
        InputStream inputStream,
        OutputStream outputStream,
        FileUploadCallback callback
    ) {
        try {
            resourceUploadManager.drainAsync(
                inputStream,
                outputStream,
                new EzyResultCallback<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        callback.onSuccess();
                        asyncContext.complete();
                    }
                    @Override
                    public void onException(Exception e) {
                        callback.onFailure(e);
                        asyncContext.complete();
                    }
                });
        }
        catch (Exception e) {
            callback.onFailure(e);
            asyncContext.complete();
        }
    }
}
