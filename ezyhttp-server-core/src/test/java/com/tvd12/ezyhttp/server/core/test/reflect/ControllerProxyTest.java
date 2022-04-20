package com.tvd12.ezyhttp.server.core.test.reflect;

import java.util.List;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.annotation.EzyFeature;
import com.tvd12.ezyfox.annotation.EzyManagement;
import com.tvd12.ezyfox.annotation.EzyPayment;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerMethod;
import com.tvd12.ezyhttp.server.core.test.controller.HomeController;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;

public class ControllerProxyTest extends BaseTest {

    @Test
    public void test() {
        // given
        HomeController homeController = new HomeController();
        ControllerProxy sut = new ControllerProxy(homeController);

        // when
        List<ExceptionHandlerMethod> exceptionHandlerMethods = sut.getExceptionHandlerMethods();

        // then
        Asserts.assertEquals(2, exceptionHandlerMethods.size());
        Asserts.assertFalse(sut.isPayment());
        Asserts.assertNull(sut.getFeature());
        System.out.println(sut);
    }

    @Test
    public void isPaymentAndFeatureTest() {
        // given
        ControllerProxy sut = new ControllerProxy(new InternalController());

        // when
        // then
        Asserts.assertTrue(sut.isPayment());
        Asserts.assertEquals(sut.getFeature(), "hello.world");
    }

    @Test
    public void isManagementTest() {
        // given
        ControllerProxy sut = new ControllerProxy(new InternalController());

        // when
        // then
        Asserts.assertTrue(sut.isManagement());
    }

    @EzyPayment
    @EzyManagement
    @EzyFeature("hello.world")
    public static class InternalController { }
}
