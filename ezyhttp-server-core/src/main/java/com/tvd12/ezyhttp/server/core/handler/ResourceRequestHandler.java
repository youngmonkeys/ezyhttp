package com.tvd12.ezyhttp.server.core.handler;

import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.stream.EzyAnywayInputStreamLoader;
import com.tvd12.ezyfox.stream.EzyInputStreamLoader;
import com.tvd12.ezyhttp.core.constant.ContentType;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import lombok.AllArgsConstructor;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

import static com.tvd12.ezyfox.util.EzyProcessor.processWithLogException;

@AllArgsConstructor
public class ResourceRequestHandler implements RequestHandler {

    private final String resourcePath;
    private final String resourceURI;
    private final String resourceExtension;
    private final EzyInputStreamLoader inputStreamLoader;
    private final ResourceDownloadManager downloadManager;
    private final int defaultTimeout;

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
        AsyncContext asyncContext = arguments.getAsyncContext();
        if (defaultTimeout > 0) {
            asyncContext.setTimeout(defaultTimeout);
        }
        HttpServletResponse servletResponse =
            (HttpServletResponse) asyncContext.getResponse();
        InputStream inputStream = inputStreamLoader.load(resourcePath);
        OutputStream outputStream = servletResponse.getOutputStream();
        try {
            downloadManager.drainAsync(
                inputStream,
                outputStream,
                new EzyResultCallback<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        processWithLogException(inputStream::close);
                        servletResponse.setContentType(getResponseContentType());
                        servletResponse.setStatus(StatusCodes.OK);
                        asyncContext.complete();
                    }

                    @Override
                    public void onException(Exception e) {
                        processWithLogException(inputStream::close);
                        servletResponse.setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
                        asyncContext.complete();
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
