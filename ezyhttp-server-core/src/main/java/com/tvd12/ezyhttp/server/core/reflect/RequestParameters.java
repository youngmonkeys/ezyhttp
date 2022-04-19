package com.tvd12.ezyhttp.server.core.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.core.constant.Constants;
import com.tvd12.ezyhttp.server.core.annotation.RequestArgument;

public final class RequestParameters {

    private RequestParameters() {}
    
    public static String getArgumentKeyString(Parameter parameter) {
        RequestArgument requestArgumentAnno = parameter.getAnnotation(RequestArgument.class);
        if (requestArgumentAnno != null)
            return EzyStrings.quote(requestArgumentAnno.value());
        Annotation[] annotations = parameter.getAnnotations();
        if (annotations.length > 0) {
            Class<? extends Annotation> annotationType = annotations[0].annotationType();
            return annotationType.getName() + Constants.EXTENSION_CLASS;
        }
        Class<?> parameterType = parameter.getType();
        return parameterType.getName() + Constants.EXTENSION_CLASS;
    }
    
}
