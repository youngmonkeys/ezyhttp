package com.tvd12.ezyhttp.server.core.constant;

import com.tvd12.ezyfox.constant.EzyConstant;

import lombok.Getter;

@Getter
public enum HttpMethod implements EzyConstant {

	GET(1, "get"),
	POST(2, "post"),
	PUT(3, "put"),
	DELETE(4, "delete");
	
	private final int id;
	private final String name;
	
	private HttpMethod(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
}
