package com.tvd12.ezyhttp.server.core.util;

import com.tvd12.ezyhttp.server.core.annotation.TryCatch;
import com.tvd12.ezyhttp.server.core.constant.ContentTypes;

public final class TryCatchAnnotations {

	private TryCatchAnnotations() {}
	
	public static Class<?>[] getExceptionClasses(TryCatch tryCatch) {
		Class<?>[] classes = tryCatch.value();
		return classes;
	}
	
	public static String getResponseType(TryCatch annotation) {
		String responseType = ContentTypes.APPLICATION_JSON;
		return responseType;
	}
}
