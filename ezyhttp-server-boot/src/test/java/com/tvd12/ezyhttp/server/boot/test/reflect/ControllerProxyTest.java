package com.tvd12.ezyhttp.server.boot.test.reflect;

import com.tvd12.ezyhttp.server.boot.test.controller.HomeController;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;

import org.testng.annotations.Test;

import com.tvd12.test.base.BaseTest;

public class ControllerProxyTest extends BaseTest {

    @Test
    public void test() {
        ControllerProxy home = new ControllerProxy(new HomeController());
        System.out.println(home);
    }
}
