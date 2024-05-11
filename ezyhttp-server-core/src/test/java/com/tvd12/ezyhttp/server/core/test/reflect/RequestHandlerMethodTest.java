package com.tvd12.ezyhttp.server.core.test.reflect;

import com.tvd12.ezyfox.annotation.EzyFeature;
import com.tvd12.ezyfox.annotation.EzyPayment;
import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.DoPost;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.stream.Collectors;

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

    @Test
    public void doGetAnnotationTest() throws Exception {
        // given
        RequestHandlerMethod sut = new RequestHandlerMethod(
            "/root",
            new EzyMethod(InternalController.class.getDeclaredMethod("getHello"))
        );

        // when
        // then
        Asserts.assertEquals(sut.getRequestURI(), "/root/hello");
        Asserts.assertEquals(
            sut.duplicatedToOtherRequestHandlerMethods(),
            Collections.emptySet(),
            false
        );
    }

    @Test
    public void doGetAnnotationWithOtherUrisTest() throws Exception {
        // given
        RequestHandlerMethod sut = new RequestHandlerMethod(
            "/root",
            new EzyMethod(InternalController.class.getDeclaredMethod("getHelloWithOtherUris"))
        );

        // when
        // then
        Asserts.assertEquals(sut.getRequestURI(), "/root/hello");
        Asserts.assertEquals(
            sut.duplicatedToOtherRequestHandlerMethods()
                .stream()
                .map(RequestHandlerMethod::getRequestURI)
                .collect(Collectors.toSet()),
            Sets.newHashSet(
                "/root/world",
                "/root/foo",
                "/root/bar"
            ),
            false
        );
        Asserts.assertEquals(sut.getRootURI(), "/root");
    }

    public static class InternalController {

        @EzyPayment
        @EzyFeature("hello.world")
        @DoPost
        public void buySomething() {}

        @DoPost
        public void getSomething() {}

        @DoGet(value = "/hello")
        public void getHello() {}

        @DoGet(value = "/hello", otherUris = {"/world", "/foo", "/bar"})
        public void getHelloWithOtherUris() {}
    }
}
