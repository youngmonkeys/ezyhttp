package com.tvd12.ezyhttp.core.response;

import java.util.Map;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

import lombok.Getter;

@Getter
public class ResponseEntity<T> {
	
	protected final int status;
	protected final Map<String, String> headers;
	protected final T body;
	
	public ResponseEntity() {
		this(StatusCodes.OK);
	}
	
	public ResponseEntity(int status) {
		this(status, null);
	}
	
	public ResponseEntity(int status, Map<String, String> headers) {
		this(status, headers, null);
	}
	
	public ResponseEntity(int status, Map<String, String> headers, T body) {
		this.status = status;
		this.headers = headers;
		this.body = body;
	}
	
	public String getHeader(String name) {
		String value = headers.get(name);
		return value;
	}
	
}
