package com.tvd12.ezyhttp.server.core.test.reflect;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.annotation.EzyFeature;
import com.tvd12.ezyfox.annotation.EzyPayment;
import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.server.core.annotation.DoPost;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;

public class RequestHandlerMethodTest extends BaseTest {

    @Test
    public void test() throws Exception {
        // given
        RequestHandlerMethod sut = new RequestHandlerMethod(
            "/get",
            new EzyMethod(InternalController.class.getDeclaredMethod("getSomething"))
        );

        // when
        // then
        Asserts.assertFalse(sut.isPayment());
        Asserts.assertNull(sut.getFeature());
        System.out.println(sut);
    }

    @Test
    public void isPaymentAndFeatureTest() throws Exception {
        // given
        RequestHandlerMethod sut = new RequestHandlerMethod(
            "/get",
            new EzyMethod(InternalController.class.getDeclaredMethod("buySomething"))
        );

        // when
        // then
        Asserts.assertTrue(sut.isPayment());
        Asserts.assertEquals(sut.getFeature(), "hello.world");
    }

    public static class InternalController {

        @EzyPayment
        @EzyFeature("hello.world")
        @DoPost
        public void buySomething() {}

        @DoPost
        public void getSomething() {}
    }
}
