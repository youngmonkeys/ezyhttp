package com.tvd12.ezyhttp.core.exception;

public class DeserializeCookieException extends DeserializeValueException {
	private static final long serialVersionUID = 4631812842672838327L;
	
	public DeserializeCookieException(
			String name, Object value, Class<?> outType, Exception e) {
		super(name, value, outType, e);
	}
}
