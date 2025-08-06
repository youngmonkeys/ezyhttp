package com.tvd12.ezyhttp.server.core.test.reflect;

import com.tvd12.ezyfox.annotation.EzyFeature;
import com.tvd12.ezyfox.annotation.EzyManagement;
import com.tvd12.ezyfox.annotation.EzyPayment;
import com.tvd12.ezyhttp.server.core.annotation.Authenticatable;
import com.tvd12.ezyhttp.server.core.handler.*;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerMethod;
import com.tvd12.ezyhttp.server.core.test.controller.HomeController;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;

import java.util.List;

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

    @Test
    public void testWithInterfaces() {
        // given
        InternalController2 internalController2 = new InternalController2();
        ControllerProxy sut = new ControllerProxy(internalController2);

        // when
        // then
        Asserts.assertTrue(sut.isManagement());
        Asserts.assertTrue(sut.isApi());
        Asserts.assertTrue(sut.isAuthenticated());
        Asserts.assertTrue(sut.isAuthenticatable());
        Asserts.assertTrue(sut.isPayment());
        Asserts.assertEquals(sut.getFeature(), "hello.world");
        System.out.println(sut);
    }

    @Test
    public void testWithNothing() {
        // given
        InternalController3 internalController3 = new InternalController3();
        ControllerProxy sut = new ControllerProxy(internalController3);

        // when
        // then
        Asserts.assertFalse(sut.isManagement());
        Asserts.assertFalse(sut.isApi());
        Asserts.assertFalse(sut.isAuthenticated());
        Asserts.assertFalse(sut.isAuthenticatable());
        Asserts.assertFalse(sut.isPayment());
        Asserts.assertNull(sut.getFeature());
        System.out.println(sut);
    }

    @Test
    public void testWithAuthenticatable() {
        // given
        InternalController4 internalController4 = new InternalController4();
        ControllerProxy sut = new ControllerProxy(internalController4);

        // when
        // then
        Asserts.assertTrue(sut.isAuthenticatable());
    }

    @EzyPayment
    @EzyManagement
    @EzyFeature("hello.world")
    public static class InternalController { }

    public static class InternalController2 implements
        ManageableController,
        ApiController,
        AuthenticatedController,
        AuthenticatableController,
        PaymentController,
        FeatureController {

        @Override
        public boolean isManagement() {
            return true;
        }

        @Override
        public boolean isApi() {
            return true;
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public boolean isAuthenticatable() {
            return true;
        }

        @Override
        public boolean isPayment() {
            return true;
        }

        @Override
        public String getFeature() {
            return "hello.world";
        }
    }

    public static class InternalController3 {}

    @Authenticatable
    public static class InternalController4 {}
}
