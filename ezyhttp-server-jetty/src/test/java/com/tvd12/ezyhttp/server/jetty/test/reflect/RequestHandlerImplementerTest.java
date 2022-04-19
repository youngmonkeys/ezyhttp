package com.tvd12.ezyhttp.server.jetty.test.reflect;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.asm.RequestHandlerImplementer;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;
import com.tvd12.ezyhttp.server.jetty.test.controller.HomeController;
import com.tvd12.test.base.BaseTest;

public class RequestHandlerImplementerTest extends BaseTest {
    
    @Test
    public void test() {
        RequestHandlerImplementer.setDebug(true);
        ControllerProxy homeController = new ControllerProxy(new HomeController());
        for (RequestHandlerMethod method : homeController.getRequestHandlerMethods()) {
            RequestHandlerImplementer implementer = new RequestHandlerImplementer(homeController, method);
            implementer.implement();
        }
    }

}
