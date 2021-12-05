package com.tvd12.ezyhttp.server.management.data;

import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public class JavaMethod {
    private final String name;
    private final String clazz;
    private final String packet;

    public JavaMethod(Method method) {
        this.name = method.getName();
        this.clazz = method.getDeclaringClass().getSimpleName();
        this.packet = method.getDeclaringClass().getPackage().getName();
    }
}
