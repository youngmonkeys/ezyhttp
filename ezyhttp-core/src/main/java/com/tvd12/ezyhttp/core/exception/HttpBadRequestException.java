package com.tvd12.ezyhttp.core.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

public class HttpBadRequestException extends HttpRequestException {
    private static final long serialVersionUID = -3918109494364067705L;

    public HttpBadRequestException(Object data) {
        super(StatusCodes.BAD_REQUEST, data);
    }

}
