package com.tvd12.ezyhttp.server.core.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.function.EzyVoid;
import com.tvd12.ezyfox.util.EzyFileUtil;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.EzyProcessor;
import com.tvd12.ezyhttp.core.constant.StatusCodes;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileUploader extends EzyLoggable {

    private final ResourceUploadManager resourceUploadManager;
    
    public void accept(
        AsyncContext asyncContext,
        Part part,
        File outputFile,
        EzyVoid callback
    ) {
        accept(asyncContext, part, outputFile, new FileUploadCallback() {
            @Override
            public void onSuccess() {
                callback.apply();
            }
            
            @Override
            public void onFailure(Exception e) {
                HttpServletResponse response = 
                        (HttpServletResponse)asyncContext.getResponse();
                response.setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
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
