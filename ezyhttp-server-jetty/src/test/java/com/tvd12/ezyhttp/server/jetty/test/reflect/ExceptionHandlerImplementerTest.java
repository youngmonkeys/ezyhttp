package com.tvd12.ezyhttp.server.jetty.test.reflect;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.asm.ExceptionHandlerImplementer;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerMethod;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerProxy;
import com.tvd12.ezyhttp.server.jetty.test.controller.GlobalExceptionHandler;
import com.tvd12.test.base.BaseTest;

public class ExceptionHandlerImplementerTest extends BaseTest {

    @Test
    public void test() {
        ExceptionHandlerImplementer.setDebug(true);
        ExceptionHandlerProxy exceptionHandler = new ExceptionHandlerProxy(new GlobalExceptionHandler());
        for(ExceptionHandlerMethod method : exceptionHandler.getExceptionHandlerMethods()) {
            ExceptionHandlerImplementer implementer = new ExceptionHandlerImplementer(exceptionHandler, method);
            implementer.implement();
        }
    }

}
