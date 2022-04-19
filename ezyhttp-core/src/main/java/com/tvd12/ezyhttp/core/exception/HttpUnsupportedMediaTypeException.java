package com.tvd12.ezyhttp.core.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

public class HttpUnsupportedMediaTypeException extends HttpRequestException {
    private static final long serialVersionUID = -3918109494364067705L;

    public HttpUnsupportedMediaTypeException(Object data) {
        super(StatusCodes.UNSUPPORTED_MEDIA_TYPE, data);
    }

}
