package com.tvd12.ezyhttp.server.core;

import java.lang.annotation.Annotation;
import java.util.List;

public interface ApplicationContext {
	
	<T> T getSingleton(Class<? extends Annotation> annotationClass);
	
	List<Object> getSingletons(Class<? extends Annotation> annotationClass);
	
}
