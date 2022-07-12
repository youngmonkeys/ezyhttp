package com.tvd12.ezyhttp.server.tomcat.test.reflect;

import com.tvd12.ezyhttp.server.core.asm.RequestHandlerImplementer;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;
import com.tvd12.ezyhttp.server.tomcat.test.controller.HomeController;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;

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
