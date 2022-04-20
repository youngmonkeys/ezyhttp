package com.tvd12.ezyhttp.server.core.resources;

import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.function.EzyExceptionVoid;
import com.tvd12.ezyfox.util.EzyFileUtil;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.MaxUploadSizeException;
import com.tvd12.ezyhttp.core.resources.ResourceUploadManager;
import lombok.AllArgsConstructor;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.tvd12.ezyfox.util.EzyProcessor.processWithLogException;
import static com.tvd12.ezyhttp.core.resources.ResourceUploadManager.UNLIMITED_UPLOAD_SIZE;

@AllArgsConstructor
public class FileUploader extends EzyLoggable {

    private final ResourceUploadManager resourceUploadManager;
    private final int defaultTimeout;

    private static final byte[] OVER_UPLOAD_SIZE_MESSAGE =
            "{\"uploadSize\":\"over\"}".getBytes();

    public FileUploader(ResourceUploadManager resourceUploadManager) {
        this(resourceUploadManager, 0);
    }

    public void accept(
            AsyncContext asyncContext,
            Part part,
            File outputFile,
            EzyExceptionVoid callback
    ) {
        accept(
            asyncContext,
            part,
            outputFile,
            UNLIMITED_UPLOAD_SIZE,
            callback
        );
    }

    public void accept(
            AsyncContext asyncContext,
            Part part,
            File outputFile,
            long maxUploadSize,
            EzyExceptionVoid callback
    ) {
        HttpServletResponse response =
                (HttpServletResponse) asyncContext.getResponse();
        accept(
                asyncContext,
                part,
                outputFile,
                maxUploadSize,
                new FileUploadCallback() {
                    @Override
                    public void onSuccess() {
                        try {
                            callback.apply();
                            response.setStatus(StatusCodes.OK);
                        } catch (Exception e) {
                            onFailure(e);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (e instanceof MaxUploadSizeException) {
                            processWithLogException(() ->
                                    response.getOutputStream().write(OVER_UPLOAD_SIZE_MESSAGE)
                            );
                            response.setStatus(StatusCodes.BAD_REQUEST);
                        } else {
                            response.setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
                        }
                        HttpServletRequest request =
                                (HttpServletRequest) asyncContext.getRequest();
                        logger.info("accept request: {} error", request.getRequestURI(), e);
                    }
                }
        );
    }

    public void accept(
            AsyncContext asyncContext,
            Part part,
            File outputFile,
            FileUploadCallback callback
    ) {
        accept(
            asyncContext,
            part,
            outputFile,
            UNLIMITED_UPLOAD_SIZE,
            callback
        );
    }

    public void accept(
            AsyncContext asyncContext,
            Part part,
            File outputFile,
            long maxUploadSize,
            FileUploadCallback callback
    ) {
        try {
            accept(
                    asyncContext,
                    part.getInputStream(),
                    outputFile,
                    maxUploadSize,
                    callback
            );
        } catch (Exception e) {
            try {
                callback.onFailure(e);
            } finally {
                processWithLogException(asyncContext::complete);
            }
        }
    }

    public void accept(
            AsyncContext asyncContext,
            InputStream inputStream,
            File outputFile,
            FileUploadCallback callback
    ) {
        accept(
            asyncContext,
            inputStream,
            outputFile,
            UNLIMITED_UPLOAD_SIZE,
            callback
        );
    }

    public void accept(
            AsyncContext asyncContext,
            InputStream inputStream,
            File outputFile,
            long maxUploadSize,
            FileUploadCallback callback
    ) {
        try {
            EzyFileUtil.createFileIfNotExists(outputFile);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            accept(
                    asyncContext,
                    inputStream,
                    outputStream,
                    maxUploadSize,
                    new FileUploadCallback() {
                        @Override
                        public void onSuccess() {
                            processWithLogException(outputStream::close);
                            callback.onSuccess();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            processWithLogException(outputStream::close);
                            callback.onFailure(e);
                        }
                    });
        } catch (Exception e) {
            try {
                callback.onFailure(e);
            } finally {
                processWithLogException(asyncContext::complete);
            }
        }
    }

    public void accept(
            AsyncContext asyncContext,
            InputStream inputStream,
            OutputStream outputStream,
            FileUploadCallback callback
    ) {
        accept(
            asyncContext,
            inputStream,
            outputStream,
            UNLIMITED_UPLOAD_SIZE,
            callback
        );
    }

    public void accept(
            AsyncContext asyncContext,
            InputStream inputStream,
            OutputStream outputStream,
            long maxUploadSize,
            FileUploadCallback callback
    ) {
        try {
            if (defaultTimeout > 0) {
                asyncContext.setTimeout(defaultTimeout);
            }
            resourceUploadManager.drainAsync(
                    inputStream,
                    outputStream,
                    maxUploadSize,
                    new EzyResultCallback<Boolean>() {
                        @Override
                        public void onResponse(Boolean response) {
                            try {
                                callback.onSuccess();
                            } finally {
                                asyncContext.complete();
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            try {
                                callback.onFailure(e);
                            } finally {
                                asyncContext.complete();
                            }
                        }
                    });
        } catch (Exception e) {
            try {
                callback.onFailure(e);
            } finally {
                processWithLogException(asyncContext::complete);
            }
        }
    }
}
