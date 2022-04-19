package com.tvd12.ezyhttp.server.core.reflect;

import java.util.ArrayList;
import java.util.List;

import com.tvd12.ezyfox.reflect.EzyClass;
import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;

import lombok.Getter;

@Getter
public class ExceptionHandlerProxy {

    protected final EzyClass clazz;
    protected final Object instance;
    protected final List<ExceptionHandlerMethod> exceptionHandlerMethods;
    
    public ExceptionHandlerProxy(Object instance) {
        this.instance = instance;
        this.clazz = new EzyClass(instance.getClass());
        this.exceptionHandlerMethods = fetchExceptionHandlerMethods();
    }
    
    public List<ExceptionHandlerMethod> fetchExceptionHandlerMethods() {
        List<ExceptionHandlerMethod> list = new ArrayList<>();
        List<EzyMethod> methods = clazz.getPublicMethods(m -> m.isAnnotated(TryCatch.class));
        for (EzyMethod method : methods) {
            ExceptionHandlerMethod m = new ExceptionHandlerMethod(method);
            list.add(m);
        }
        return list;
    }
    
    public String getClassSimpleName() {
        return clazz.getClazz().getSimpleName();
    }
    
    @Override
    public String toString() {
        return clazz.getName() +
            "(\n" +
            "\tinstance: " + instance + ",\n" +
            "\texceptionHandlerMethods: " + exceptionHandlerMethods + "\n" +
            ")";
    }
    
}
