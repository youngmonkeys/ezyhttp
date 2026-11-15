package com.tvd12.ezyhttp.server.core;

import java.lang.annotation.Annotation;
import java.util.List;

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfox.util.EzyLoggable;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unchecked")
public class SimpleApplicationContext
    extends EzyLoggable
    implements ApplicationContext {

    @Setter
    @Getter
    protected EzyBeanContext beanContext;

    @Override
    public <T> T getProperty(Object key, Class<T> outType) {
        return beanContext.getProperty(key, outType);
    }

    @Override
    public <T> T getProperty(Object key, Class<T> outType, T defaultValue) {
        return beanContext.getProperty(key, outType, defaultValue);
    }

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

    @SuppressWarnings("rawtypes")
    @Override
    public void destroy() {
        List destroyableComponents = beanContext.getSingletonsOf(EzyDestroyable.class);
        for (Object component : destroyableComponents) {
            try {
                ((EzyDestroyable) component).destroy();
            } catch (Throwable e) {
                logger.error(
                    "destroy component: {} error",
                    component.getClass().getName(),
                    e
                );
            }
        }
    }
}
