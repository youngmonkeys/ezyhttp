package com.tvd12.ezyhttp.core.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

public class HttpTooManyRequestsException extends HttpRequestException {
	private static final long serialVersionUID = -3918109494364067705L;

	public HttpTooManyRequestsException(Object data) {
		super(StatusCodes.TOO_MANY_REQUESTS, data);
	}
}
