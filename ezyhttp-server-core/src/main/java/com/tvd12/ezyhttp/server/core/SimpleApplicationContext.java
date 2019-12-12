package com.tvd12.ezyhttp.server.core;

import java.lang.annotation.Annotation;
import java.util.List;

import com.tvd12.ezyfox.bean.EzyBeanContext;

import lombok.Setter;

@SuppressWarnings("unchecked")
public class SimpleApplicationContext implements ApplicationContext {
	
	@Setter
	protected EzyBeanContext beanContext;

	@Override
	public <T> T getSingleton(Class<? extends Annotation> annotationClass) {
		List<Object> singletons = getSingletons(annotationClass);
		if(singletons.isEmpty())
			return null;
		T singleton = (T)singletons.get(0);
		return singleton;
	}
	
	@Override
	public List<Object> getSingletons(Class<? extends Annotation> annotationClass) {
		List<Object> singletons = beanContext.getSingletons(annotationClass);
		return singletons;
	}
	
}
