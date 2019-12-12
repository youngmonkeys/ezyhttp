package com.tvd12.ezyhttp.server.core.util;

import com.tvd12.ezyhttp.core.annotation.Interceptor;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;

public final class InterceptorAnnotations {

	private InterceptorAnnotations() {}
	
	public static int getPriority(Interceptor annotation) {
		int priority = annotation.priority();
		return priority;
	}
	
	public static int getPriority(RequestInterceptor interceptor) {
		Interceptor annotation = interceptor.getClass().getAnnotation(Interceptor.class);
		return getPriority(annotation);
	}
	
}
