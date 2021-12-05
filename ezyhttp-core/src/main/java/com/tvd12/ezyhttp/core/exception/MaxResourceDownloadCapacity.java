package com.tvd12.ezyhttp.core.exception;

public class MaxResourceDownloadCapacity extends IllegalStateException {
	private static final long serialVersionUID = -3720935602307338922L;
	
	public MaxResourceDownloadCapacity(int capacity) {
		super("max resource download capacity: " + capacity);
	}

}
