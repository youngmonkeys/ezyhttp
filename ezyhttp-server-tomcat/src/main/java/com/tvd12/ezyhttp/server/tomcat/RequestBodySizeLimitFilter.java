package com.tvd12.ezyhttp.server.tomcat;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.exception.HttpRequestException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RequestBodySizeLimitFilter implements Filter {

    private final long maxRequestBodySize;
    private final long maxMultipartRequestSize;

    public static final int PAYLOAD_TOO_LARGE = 413;

    public RequestBodySizeLimitFilter(
        long maxRequestBodySize,
        long maxMultipartRequestSize
    ) {
        this.maxRequestBodySize = maxRequestBodySize;
        this.maxMultipartRequestSize = maxMultipartRequestSize;
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        boolean multipart = isMultipartRequest(httpRequest);
        long limit = multipart
            ? maxMultipartRequestSize
            : maxRequestBodySize;
        if (limit < 0) {
            chain.doFilter(request, response);
            return;
        }
        if (httpRequest.getContentLengthLong() > limit) {
            ((HttpServletResponse) response).setStatus(PAYLOAD_TOO_LARGE);
            return;
        }
        chain.doFilter(
            multipart
                ? request
                : new SizeLimitHttpServletRequest(httpRequest, limit),
            response
        );
    }

    @Override
    public void destroy() {}

    private static boolean isMultipartRequest(HttpServletRequest request) {
        String contentType = ContentTypes.getContentType(
            request.getContentType()
        );
        return contentType != null
            && ContentTypes.MULTIPART_FORM_DATA.equalsIgnoreCase(
                contentType.trim()
            );
    }

    private static void checkRequestBodySize(long size, long limit) {
        if (size > limit) {
            throw new HttpRequestException(
                PAYLOAD_TOO_LARGE,
                "Request body is too large: " + size + ">" + limit
            );
        }
    }

    private static class SizeLimitHttpServletRequest
        extends HttpServletRequestWrapper {

        private final long limit;
        private ServletInputStream inputStream;

        SizeLimitHttpServletRequest(
            HttpServletRequest request,
            long limit
        ) {
            super(request);
            this.limit = limit;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (inputStream == null) {
                inputStream = new SizeLimitServletInputStream(
                    super.getInputStream(),
                    limit
                );
            }
            return inputStream;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            String encoding = getCharacterEncoding();
            return new BufferedReader(
                new InputStreamReader(
                    getInputStream(),
                    encoding != null
                        ? encoding
                        : StandardCharsets.ISO_8859_1.name()
                )
            );
        }
    }

    private static class SizeLimitServletInputStream
        extends ServletInputStream {

        private final ServletInputStream inputStream;
        private final long limit;
        private long read;

        SizeLimitServletInputStream(
            ServletInputStream inputStream,
            long limit
        ) {
            this.inputStream = inputStream;
            this.limit = limit;
        }

        @Override
        public int read() throws IOException {
            int value = inputStream.read();
            if (value >= 0) {
                addRead(1);
            }
            return value;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int count = inputStream.read(b, off, len);
            if (count > 0) {
                addRead(count);
            }
            return count;
        }

        private void addRead(int count) {
            read += count;
            checkRequestBodySize(read, limit);
        }

        @Override
        public boolean isFinished() {
            return inputStream.isFinished();
        }

        @Override
        public boolean isReady() {
            return inputStream.isReady();
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            inputStream.setReadListener(readListener);
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }
    }
}
