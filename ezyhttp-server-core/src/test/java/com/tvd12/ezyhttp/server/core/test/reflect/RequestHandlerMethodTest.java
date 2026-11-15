package com.tvd12.ezyhttp.server.core.test.reflect;

import com.tvd12.ezyfox.annotation.EzyFeature;
import com.tvd12.ezyfox.annotation.EzyPayment;
import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.annotation.DoDelete;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.DoHead;
import com.tvd12.ezyhttp.server.core.annotation.DoPost;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
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

    @Test
    public void doDeleteAnnotationTest() throws Exception {
        // given
        RequestHandlerMethod sut = new RequestHandlerMethod(
            "/root",
            new EzyMethod(InternalController.class.getDeclaredMethod("deleteHello"))
        );

        // when
        // then
        Asserts.assertEquals(sut.getRequestURI(), "/root/hello");
        Asserts.assertEquals(sut.getHttpMethod(), HttpMethod.DELETE);
        Asserts.assertEquals(sut.getResponseType(), ContentTypes.TEXT_PLAIN);
        Asserts.assertEquals(
            sut.getAccept(),
            Sets.newHashSet(ContentTypes.APPLICATION_JSON),
            false
        );
        Asserts.assertEquals(
            sut.duplicatedToOtherRequestHandlerMethods(),
            Collections.emptyList(),
            false
        );
    }

    @Test
    public void doHeadAnnotationTest() throws Exception {
        // given
        RequestHandlerMethod sut = new RequestHandlerMethod(
            "/root",
            new EzyMethod(InternalController.class.getDeclaredMethod("headHello"))
        );

        // when
        // then
        Asserts.assertEquals(sut.getRequestURI(), "/root/hello");
        Asserts.assertEquals(sut.getHttpMethod(), HttpMethod.HEAD);
        Asserts.assertEquals(sut.getResponseType(), ContentTypes.TEXT_PLAIN);
        Asserts.assertEquals(
            sut.getAccept(),
            Sets.newHashSet(ContentTypes.APPLICATION_JSON),
            false
        );
        Asserts.assertEquals(
            sut.duplicatedToOtherRequestHandlerMethods(),
            Collections.emptyList(),
            false
        );
    }

    @Test
    public void doHeadAnnotationWithOtherUrisTest() throws Exception {
        // given
        RequestHandlerMethod sut = new RequestHandlerMethod(
            "/root",
            new EzyMethod(InternalController.class.getDeclaredMethod("headHelloWithOtherUris"))
        );

        // when
        List<RequestHandlerMethod> others = sut.duplicatedToOtherRequestHandlerMethods();

        // then
        Asserts.assertEquals(sut.getRequestURI(), "/root/hello");
        Asserts.assertEquals(sut.getHttpMethod(), HttpMethod.HEAD);
        Asserts.assertEquals(
            others
                .stream()
                .map(RequestHandlerMethod::getRequestURI)
                .collect(Collectors.toSet()),
            Sets.newHashSet(
                "/root/world",
                "/root/foo"
            ),
            false
        );
        Asserts.assertEquals(
            others
                .stream()
                .map(RequestHandlerMethod::getHttpMethod)
                .collect(Collectors.toSet()),
            Sets.newHashSet(HttpMethod.HEAD),
            false
        );
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

        @DoDelete(
            value = "/hello",
            accept = "application/json",
            responseType = ContentTypes.TEXT_PLAIN
        )
        public void deleteHello() {}

        @DoHead(
            value = "/hello",
            accept = "application/json",
            responseType = ContentTypes.TEXT_PLAIN
        )
        public void headHello() {}

        @DoHead(value = "/hello", otherUris = {"/world", "", "/foo"})
        public void headHelloWithOtherUris() {}
    }
}
