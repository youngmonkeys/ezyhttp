package com.tvd12.ezyhttp.server.tomcat.test.reflect;

import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.tomcat.test.controller.HomeController;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;

public class ControllerProxyTest extends BaseTest {

    @Test
    public void test() {
        ControllerProxy home = new ControllerProxy(new HomeController());
        System.out.println(home);
    }
}
