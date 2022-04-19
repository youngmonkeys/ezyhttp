package com.tvd12.ezyhttp.server.core.util;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.reflect.EzyClasses;
import com.tvd12.ezyhttp.server.core.annotation.Service;

public final class ServiceAnnotations {

    private ServiceAnnotations() {}

    public static String getServiceName(Class<?> serviceClass) {
        Service annotation = serviceClass.getAnnotation(Service.class);
        if (!EzyStrings.isNoContent(annotation.value()))
            return annotation.value();
        if (!EzyStrings.isNoContent(annotation.name()))
            return annotation.name();
        return EzyClasses.getVariableName(serviceClass, "Impl");
    }

}
