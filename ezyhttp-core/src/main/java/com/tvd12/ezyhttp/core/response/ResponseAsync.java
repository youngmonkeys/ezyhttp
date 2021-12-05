package com.tvd12.ezyhttp.core.response;

public final class ResponseAsync {

	private static final ResponseAsync INSTANCE = new ResponseAsync();
	
	private ResponseAsync() {}
	
	public static ResponseAsync getInstance() {
		return INSTANCE;
	}
}
