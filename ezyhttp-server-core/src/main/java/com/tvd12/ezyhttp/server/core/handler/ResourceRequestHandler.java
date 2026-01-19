package com.tvd12.ezyhttp.server.core.handler;

import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.exception.EzyFileNotFoundException;
import com.tvd12.ezyfox.stream.EzyAnywayInputStreamLoader;
import com.tvd12.ezyfox.stream.EzyInputStreamLoader;
import com.tvd12.ezyhttp.core.constant.*;
import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import com.tvd12.ezyhttp.core.io.BytesRangeFileInputStream;
import com.tvd12.ezyhttp.core.resources.ActualContentTypeDetector;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import lombok.AllArgsConstructor;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static com.tvd12.ezyfox.util.EzyProcessor.processWithLogException;

@AllArgsConstructor
public class ResourceRequestHandler implements RequestHandler {

    private final String resourcePath;
    private final String resourceURI;
    private final String resourceExtension;
    private final EzyInputStreamLoader inputStreamLoader;
    private final ResourceDownloadManager downloadManager;
    private final int defaultTimeout;
    private final EzyResultCallback<Boolean> callback;
    private final ActualContentTypeDetector actualContentTypeDetector =
        ActualContentTypeDetector.getInstance();

    public ResourceRequestHandler(
        String resourcePath,
        String resourceURI,
        String resourceExtension,
        EzyInputStreamLoader inputStreamLoader,
        ResourceDownloadManager downloadManager,
        int defaultTimeout
    ) {
        this(
            resourcePath,
            resourceURI,
            resourceExtension,
            inputStreamLoader,
            downloadManager,
            defaultTimeout,
            null
        );
    }

    public ResourceRequestHandler(
        String resourcePath,
        String resourceURI,
        String resourceExtension,
        ResourceDownloadManager downloadManager
    ) {
        this(
            resourcePath,
            resourceURI,
            resourceExtension,
            downloadManager,
            0
        );
    }

    public ResourceRequestHandler(
        String resourcePath,
        String resourceURI,
        String resourceExtension,
        EzyInputStreamLoader inputStreamLoader,
        ResourceDownloadManager downloadManager
    ) {
        this(
            resourcePath,
            resourceURI,
            resourceExtension,
            inputStreamLoader,
            downloadManager,
            0
        );
    }

    public ResourceRequestHandler(
        String resourcePath,
        String resourceURI,
        String resourceExtension,
        ResourceDownloadManager downloadManager,
        int defaultTimeout
    ) {
        this(
            resourcePath,
            resourceURI,
            resourceExtension,
            new EzyAnywayInputStreamLoader(),
            downloadManager,
            defaultTimeout
        );
    }

    @Override
    public Object handle(RequestArguments arguments) throws Exception {
        final AsyncContext asyncContext = arguments.getAsyncContext();
        try {
            return doHandle(asyncContext, arguments);
        } catch (Throwable e) {
            processWithLogException(asyncContext::complete);
            if (e instanceof FileNotFoundException
                || e instanceof EzyFileNotFoundException
            ) {
                throw new HttpNotFoundException(e);
            }
            throw e;
        }
    }

    @SuppressWarnings("MethodLength")
    protected Object doHandle(
        AsyncContext asyncContext,
        RequestArguments arguments
    ) throws Exception {
        if (defaultTimeout > 0) {
            asyncContext.setTimeout(defaultTimeout);
        }
        final HttpServletResponse servletResponse =
            (HttpServletResponse) asyncContext.getResponse();
        String contentType = getResponseContentType();
        String actualContentType = actualContentTypeDetector
            .detect(resourcePath, contentType);
        servletResponse.setContentType(actualContentType);
        ContentEncoding contentEncoding = ContentEncoding
            .ofMimeType(contentType);
        if (contentEncoding != null) {
            servletResponse.setHeader(
                Headers.CONTENT_ENCODING,
                contentEncoding.getValue()
            );
        }
        final InputStream inputStream;
        int statusCode = StatusCodes.OK;
        final String range = arguments.getHeader("Range");
        if (isBlank(range)) {
            inputStream = inputStreamLoader.load(resourcePath);
            if (inputStream == null) {
                throw new FileNotFoundException(resourcePath + " file not found");
            }
        } else {
            BytesRangeFileInputStream is = null;
            try {
                is = new BytesRangeFileInputStream(
                    resourcePath,
                    range
                );
                servletResponse.setHeader(
                    Headers.ACCEPT_RANGES,
                    "bytes"
                );
                servletResponse.setHeader(
                    Headers.CONTENT_RANGE,
                    is.getBytesContentRangeString()
                );
                servletResponse.setHeader(
                    Headers.CONTENT_LENGTH,
                    String.valueOf(is.getTargetReadBytes())
                );
                statusCode = StatusCodes.PARTIAL_CONTENT;
                servletResponse.setStatus(statusCode);
                inputStream = is;
            } catch (Exception e) {
                if (is != null) {
                    is.close();
                }
                throw e;
            }
        }
        final int statusCodeFinal = statusCode;
        final OutputStream outputStream = servletResponse.getOutputStream();
        try {
            downloadManager.drainAsync(
                inputStream,
                outputStream,
                new EzyResultCallback<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        processWithLogException(inputStream::close);
                        servletResponse.setStatus(statusCodeFinal);
                        asyncContext.complete();
                        if (callback != null) {
                            callback.onResponse(response);
                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        processWithLogException(inputStream::close);
                        servletResponse.setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
                        asyncContext.complete();
                        if (callback != null) {
                            callback.onException(e);
                        }
                    }
                }
            );
        } catch (Exception e) {
            processWithLogException(inputStream::close);
            servletResponse.setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
            asyncContext.complete();
        }
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
        return resourceURI;
    }

    @Override
    public String getResponseContentType() {
        return ContentType.ofExtension(resourceExtension).getValue();
    }
}
