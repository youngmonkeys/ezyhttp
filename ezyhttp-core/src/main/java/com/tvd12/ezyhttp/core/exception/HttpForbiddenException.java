package com.tvd12.ezyhttp.core.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

public class HttpForbiddenException extends HttpRequestException {
	private static final long serialVersionUID = -3918109494364067705L;

	public HttpForbiddenException(Object data) {
		super(StatusCodes.FORBIDDEN, data);
	}
	
}
