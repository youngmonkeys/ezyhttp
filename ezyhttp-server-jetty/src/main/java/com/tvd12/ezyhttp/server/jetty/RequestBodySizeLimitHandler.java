package com.tvd12.ezyhttp.server.jetty;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.exception.HttpRequestException;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.HttpInput;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestBodySizeLimitHandler extends HandlerWrapper {

    private final long maxRequestBodySize;
    private final long maxMultipartRequestSize;

    public RequestBodySizeLimitHandler(
        long maxRequestBodySize,
        long maxMultipartRequestSize
    ) {
        this.maxRequestBodySize = maxRequestBodySize;
        this.maxMultipartRequestSize = maxMultipartRequestSize;
    }

    @Override
    public void handle(
        String target,
        Request baseRequest,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException, ServletException {
        long limit = getRequestBodySizeLimit(request.getContentType());
        if (limit >= 0) {
            checkRequestBodySize(baseRequest.getContentLengthLong(), limit);
            baseRequest
                .getHttpInput()
                .addInterceptor(new LimitInterceptor(limit));
        }
        super.handle(target, baseRequest, request, response);
    }

    private long getRequestBodySizeLimit(String contentType) {
        String mimeType = ContentTypes.getContentType(contentType);
        return mimeType != null
            && ContentTypes.MULTIPART_FORM_DATA
                .equalsIgnoreCase(mimeType.trim())
            ? maxMultipartRequestSize
            : maxRequestBodySize;
    }

    private static void checkRequestBodySize(long size, long limit) {
        if (size > limit) {
            throw new BadMessageException(
                HttpStatus.PAYLOAD_TOO_LARGE_413,
                "Request body is too large: " + size + ">" + limit
            );
        }
    }

    private static class LimitInterceptor implements HttpInput.Interceptor {

        private final long limit;
        private long read;

        LimitInterceptor(long limit) {
            this.limit = limit;
        }

        @Override
        public HttpInput.Content readFrom(HttpInput.Content content) {
            if (content.hasContent()) {
                read += content.remaining();
                if (read > limit) {
                    throw new HttpRequestException(
                        HttpStatus.PAYLOAD_TOO_LARGE_413,
                        "Request body is too large: " + read + ">" + limit
                    );
                }
            }
            return content;
        }
    }
}
