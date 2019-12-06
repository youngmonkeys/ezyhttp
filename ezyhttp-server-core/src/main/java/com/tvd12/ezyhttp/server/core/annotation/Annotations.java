package com.tvd12.ezyhttp.server.core.annotation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Annotations {

	public static final Set<Class<?>> REQUEST_HANDLER_ANNOTATIONS 
			= requestHandlerAnnotations();
	
	private Annotations() {}
	
	private static Set<Class<?>> requestHandlerAnnotations() {
		Set<Class<?>> set = new HashSet<>();
		set.add(DoGet.class);
		return Collections.unmodifiableSet(set);
	}
	
}
