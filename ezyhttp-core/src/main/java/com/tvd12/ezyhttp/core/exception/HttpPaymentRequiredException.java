package com.tvd12.ezyhttp.core.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

public class HttpPaymentRequiredException extends HttpRequestException {
    private static final long serialVersionUID = -3918109494364067705L;

    public HttpPaymentRequiredException(Object data) {
        super(StatusCodes.PAYMENT_REQUIRED, data);
    }

    public HttpPaymentRequiredException(Object data, Throwable cause) {
        super(StatusCodes.PAYMENT_REQUIRED, data, cause);
    }
}
