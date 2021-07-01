package com.tvd12.ezyhttp.server.core.util;

import javax.servlet.http.HttpServletRequest;

public final class HttpServletRequests {
	private HttpServletRequests() {}
	
	public static String getRootURL(HttpServletRequest request) {
		return getRootURLBuilder(request).toString();
	}
	
	public static String getRequestURL(HttpServletRequest request, String uri) {
		return getRootURLBuilder(request)
				.append(uri)
				.toString();
	}
	
	private static StringBuilder getRootURLBuilder(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder()
				.append(request.getScheme())
				.append("://")
				.append(request.getServerName());
		int port = request.getServerPort();
		if(port != 80 && port != 443) {
			builder.append(":").append(port);
		}
		return builder;
	}
}
