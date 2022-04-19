package com.tvd12.ezyhttp.core.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

public class HttpMethodNotAllowedException extends HttpRequestException {
    private static final long serialVersionUID = -3918109494364067705L;

    public HttpMethodNotAllowedException(Object data) {
        super(StatusCodes.METHOD_NOT_ALLOWED, data);
    }
}
