package com.tvd12.ezyhttp.core.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

public class HttpInternalServerErrorException extends HttpRequestException {
	private static final long serialVersionUID = 5323490750784168769L;

	public HttpInternalServerErrorException(Object data) {
		super(StatusCodes.INTERNAL_SERVER_ERROR, data);
	}
}
