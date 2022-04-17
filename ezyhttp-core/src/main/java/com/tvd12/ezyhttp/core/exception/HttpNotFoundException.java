package com.tvd12.ezyhttp.core.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

public class HttpNotFoundException extends HttpRequestException {
	private static final long serialVersionUID = -3918109494364067705L;

	public HttpNotFoundException(Object data) {
		super(StatusCodes.NOT_FOUND, data);
	}
}
