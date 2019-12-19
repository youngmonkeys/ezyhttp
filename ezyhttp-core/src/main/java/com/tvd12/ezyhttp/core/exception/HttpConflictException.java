package com.tvd12.ezyhttp.core.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

public class HttpConflictException extends HttpRequestException {
	private static final long serialVersionUID = -3918109494364067705L;

	public HttpConflictException(Object data) {
		super(StatusCodes.CONFLICT, data);
	}
	
}
