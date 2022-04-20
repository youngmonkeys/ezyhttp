package com.tvd12.ezyhttp.server.core.test.request;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.request.RequestURI;
import com.tvd12.ezyhttp.server.core.request.RequestURIMeta;
import com.tvd12.test.assertion.Asserts;

public class RequestURITest {

    @Test
    public void test() {
        // given
        RequestURI uri1 = new RequestURI(HttpMethod.GET, "/", true);
        RequestURI uri2 = new RequestURI(HttpMethod.GET, "/", true);
        RequestURI uri3 = new RequestURI(HttpMethod.POST, "/", true);
        RequestURI uri4 = new RequestURI(HttpMethod.GET, "/api", true);
        RequestURI uri5 = new RequestURI(HttpMethod.PUT, "/api/v1", true);
        RequestURI uri6 = new RequestURI(HttpMethod.GET, "", true);
        RequestURI uri11 = new RequestURI(HttpMethod.GET, "/", false, true, false, "fullPath");
        RequestURI uri12 = new RequestURI(HttpMethod.PUT, "/api/v1/", true);
        RequestURI ur122 = new RequestURI(
            HttpMethod.GET,
            "/",
            RequestURIMeta.builder()
                .api(true)
                .authenticated(true)
                .payment(true)
                .feature("hello.world")
                .build()
        );

        // when
        // then
        Asserts.assertEquals(uri1.getMethod(), HttpMethod.GET);
        Asserts.assertNotEquals(uri11, null);
        Asserts.assertEquals(uri11, uri11);
        Asserts.assertNotEquals(uri1, new Object());
        Asserts.assertEquals(uri1, uri1);
        Asserts.assertEquals(uri1, uri2);
        Asserts.assertNotEquals(uri1, uri3);
        Asserts.assertNotEquals(uri1, uri4);
        Asserts.assertNotEquals(uri1, uri5);
        Asserts.assertNotEquals(uri1, uri6);
        Asserts.assertNotEquals(uri1, uri11);
        Asserts.assertTrue(uri11.isResource());
        Asserts.assertEquals(uri11.getResourceFullPath(), "fullPath");
        Asserts.assertEquals(uri1.getSameURI(), "/");
        Asserts.assertEquals(uri5.getSameURI(), "/api/v1/");
        Asserts.assertEquals(uri12.getSameURI(), "/api/v1");
        Asserts.assertTrue(ur122.isApi());
        Asserts.assertTrue(ur122.isPayment());
        Asserts.assertEquals(ur122.getFeature(), "hello.world");
    }
}
