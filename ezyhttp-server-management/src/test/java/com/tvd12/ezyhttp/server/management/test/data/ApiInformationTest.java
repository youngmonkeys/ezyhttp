package com.tvd12.ezyhttp.server.management.test.data;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestURI;
import com.tvd12.ezyhttp.server.core.request.RequestURIMeta;
import com.tvd12.ezyhttp.server.management.data.ApiInformation;
import com.tvd12.ezyhttp.server.management.data.JavaMethod;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.MethodUtil;

public class ApiInformationTest {

    @Test
    public void createTest() {
        // given
        RequestURI requestURI = new RequestURI(
            HttpMethod.POST,
            "/api/v1/resources",
            RequestURIMeta.builder()
                .api(true)
                .authenticated(true)
                .management(true)
                .resource(true)
                .resourceFullPath("static/index.html")
                .build()
        );
        Method method1 = MethodUtil.getMethod("createTest", ApiInformationTest.class);
        Method method2 = MethodUtil.getMethod("createEmptyHandlersTest", ApiInformationTest.class);
        RequestHandler handler1 = mock(RequestHandler.class);
        when(handler1.getHandlerMethod()).thenReturn(method1);
        when(handler1.getAccept()).thenReturn(Collections.singleton("text/plain"));
        RequestHandler handler2 = mock(RequestHandler.class);
        when(handler2.getHandlerMethod()).thenReturn(method2);
        Set<String> accept = new HashSet<>(Arrays.asList("application/json", "text/html"));
        when(handler2.getAccept()).thenReturn(accept);

        // when
        ApiInformation sut = new ApiInformation(
            requestURI,
            Arrays.asList(handler1, handler2)
        );

        // then
        Asserts.assertEquals(sut.getUri(), "/api/v1/resources");
        Asserts.assertEquals(sut.getMethod(), HttpMethod.POST);
        Asserts.assertTrue(sut.isManagement());
        Asserts.assertTrue(sut.isAuthenticated());
        Asserts.assertTrue(sut.isResource());
        Asserts.assertEquals(sut.getResourcePath(), "static/index.html");
        Asserts.assertEquals(sut.getAccept(), accept);
        Asserts.assertEquals(sut.getHandlers().size(), 2);

        JavaMethod javaMethod1 = sut.getHandlers().get(0);
        Asserts.assertEquals(javaMethod1.getName(), "createTest");
        Asserts.assertEquals(javaMethod1.getClazz(), ApiInformationTest.class.getSimpleName());
        Asserts.assertEquals(
            javaMethod1.getPacket(),
            ApiInformationTest.class.getPackage().getName()
        );
        Asserts.assertEquals(sut.getHandlers().get(1).getName(), "createEmptyHandlersTest");
    }

    @Test
    public void createEmptyHandlersTest() {
        // given
        RequestURI requestURI = new RequestURI(HttpMethod.GET, "hello", false);

        // when
        ApiInformation sut = new ApiInformation(
            requestURI,
            Collections.emptyList()
        );

        // then
        Asserts.assertEquals(sut.getUri(), "/hello");
        Asserts.assertEquals(sut.getMethod(), HttpMethod.GET);
        Asserts.assertFalse(sut.isManagement());
        Asserts.assertFalse(sut.isAuthenticated());
        Asserts.assertFalse(sut.isResource());
        Asserts.assertNull(sut.getResourcePath());
        Asserts.assertTrue(sut.getAccept().isEmpty());
        Asserts.assertTrue(sut.getHandlers().isEmpty());
    }

    @Test
    public void createNullAcceptTest() {
        // given
        RequestURI requestURI = new RequestURI(HttpMethod.GET, "/hello", false);
        RequestHandler handler = mock(RequestHandler.class);
        when(handler.getHandlerMethod()).thenReturn(
            MethodUtil.getMethod("createNullAcceptTest", ApiInformationTest.class)
        );
        when(handler.getAccept()).thenReturn(null);

        // when
        ApiInformation sut = new ApiInformation(
            requestURI,
            Collections.singletonList(handler)
        );

        // then
        Asserts.assertNotNull(sut.getAccept());
        Asserts.assertTrue(sut.getAccept().isEmpty());
        Asserts.assertEquals(sut.getHandlers().size(), 1);
        Asserts.assertEquals(sut.getHandlers().get(0).getName(), "createNullAcceptTest");
    }
}
