package com.tvd12.ezyhttp.core.response;

public final class ResponseNothing {

	private static final ResponseNothing INSTANCE = new ResponseNothing();
	
	private ResponseNothing() {}
	
	public static ResponseNothing getInstance() {
		return INSTANCE;
	}
	
}
