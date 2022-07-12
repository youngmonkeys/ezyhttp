package com.tvd12.ezyhttp.server.tomcat.test.reflect;

import com.tvd12.ezyhttp.server.core.asm.ExceptionHandlerImplementer;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerMethod;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerProxy;
import com.tvd12.ezyhttp.server.tomcat.test.controller.GlobalExceptionHandler;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;

public class ExceptionHandlerImplementerTest extends BaseTest {

    @Test
    public void test() {
        ExceptionHandlerImplementer.setDebug(true);
        ExceptionHandlerProxy exceptionHandler = new ExceptionHandlerProxy(new GlobalExceptionHandler());
        for (ExceptionHandlerMethod method : exceptionHandler.getExceptionHandlerMethods()) {
            ExceptionHandlerImplementer implementer = new ExceptionHandlerImplementer(exceptionHandler, method);
            implementer.implement();
        }
    }
}
