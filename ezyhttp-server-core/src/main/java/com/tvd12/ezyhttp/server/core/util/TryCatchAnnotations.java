package com.tvd12.ezyhttp.server.core.util;

import com.tvd12.ezyhttp.server.core.annotation.TryCatch;

public final class TryCatchAnnotations {

	private TryCatchAnnotations() {}
	
	public static Class<?>[] getExceptionClasses(TryCatch tryCatch) {
		Class<?>[] classes = tryCatch.value();
		return classes;
	}
}
