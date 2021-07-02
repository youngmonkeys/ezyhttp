package com.tvd12.ezyhttp.server.core.view;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Redirect {

	private final String uri;
	private final int code;
	
	public static Redirect to(String uri) {
		return to(uri, StatusCodes.MOVED_TEMPORARILY);
	}
	
	public static Redirect to(String uri, int code) {
		return new Redirect(uri, code);
	}
	
	public boolean isURL() {
		return uri.startsWith("http://") || uri.startsWith("https://");
	}
	
}
