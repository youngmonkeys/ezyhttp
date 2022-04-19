package com.tvd12.ezyhttp.core.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

public class HttpRequestTimeoutException extends HttpRequestException {
    private static final long serialVersionUID = -3918109494364067705L;

    public HttpRequestTimeoutException(Object data) {
        super(StatusCodes.REQUEST_TIMEOUT, data);
    }

}
