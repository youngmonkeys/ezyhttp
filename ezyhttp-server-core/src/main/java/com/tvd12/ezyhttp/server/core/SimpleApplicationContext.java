package com.tvd12.ezyhttp.server.core;

import java.lang.annotation.Annotation;
import java.util.List;

import com.tvd12.ezyfox.bean.EzyBeanContext;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unchecked")
public class SimpleApplicationContext implements ApplicationContext {
	
	@Setter
	@Getter
	protected EzyBeanContext beanContext;
	
	@Override
	public <T> T getSingleton(Class<T> type) {
		return beanContext.getSingleton(type);
	}

	@Override
	public <T> T getAnnotatedSingleton(Class<? extends Annotation> annotationClass) {
		return beanContext.getAnnotatedSingleton(annotationClass);
	}
	
	@Override
	public List<Object> getSingletons(Class<? extends Annotation> annotationClass) {
		return beanContext.getSingletons(annotationClass);
	}
	
}
