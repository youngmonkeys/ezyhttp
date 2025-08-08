package com.tvd12.ezyhttp.core.exception;

import lombok.Getter;

public class HttpRequestException extends RuntimeException {
    private static final long serialVersionUID = 8065221643252546341L;

    @Getter
    protected final int code;
    protected final Object data;

    public HttpRequestException(
        int code,
        Object data
    ) {
        super("code: " + code + ", data: " + data);
        this.code = code;
        this.data = data;
    }

    public HttpRequestException(
        int code,
        Object data,
        Throwable cause
    ) {
        super("code: " + code + ", data: " + data, cause);
        this.code = code;
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) data;
    }
}
