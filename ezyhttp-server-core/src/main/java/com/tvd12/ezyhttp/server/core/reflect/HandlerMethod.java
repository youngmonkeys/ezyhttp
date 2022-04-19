package com.tvd12.ezyhttp.server.core.reflect;

import java.lang.reflect.Parameter;

import com.tvd12.ezyfox.reflect.EzyMethod;

import lombok.Getter;

public abstract class HandlerMethod {

    @Getter
    protected final EzyMethod method;

    public HandlerMethod(EzyMethod method) {
        this.method = method;
    }

    public String getName() {
        return method.getName();
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    public Parameter[] getParameters() {
        return method.getMethod().getParameters();
    }

    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }

}
