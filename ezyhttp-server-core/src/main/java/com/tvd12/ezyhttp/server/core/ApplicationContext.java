package com.tvd12.ezyhttp.server.core;

import java.lang.annotation.Annotation;
import java.util.List;

import com.tvd12.ezyfox.bean.EzyBeanContext;

public interface ApplicationContext {
	
	EzyBeanContext getBeanContext();
	
	<T> T getSingleton(Class<T> type);
	
	<T> T getAnnotatedSingleton(Class<? extends Annotation> annotationClass);
	
	List<Object> getSingletons(Class<? extends Annotation> annotationClass);
	
}
