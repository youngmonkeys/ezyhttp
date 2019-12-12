package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.annotation.ExceptionHandler;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;

@ExceptionHandler
public class GlobalExceptionHandler {

	@TryCatch(IllegalArgumentException.class)
	public ResponseEntity<String> handleException(Exception e) {
		return new ResponseEntity<>(StatusCodes.BAD_REQUEST, null, "global: " + e.getMessage());
	}
	
	@TryCatch({IllegalStateException.class, NullPointerException.class})
	public String handleException2(Exception e) {
		return e.getMessage();
	}
	
	@TryCatch(java.lang.UnsupportedOperationException.class)
	public void handleException3(Exception e) {
	}
	
}
