package com.tvd12.ezyhttp.server.core.test.manager;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.manager.FeatureURIManager;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.core.request.RequestURI;
import com.tvd12.ezyhttp.server.core.request.RequestURIMeta;
import com.tvd12.test.assertion.Asserts;

import static org.mockito.Mockito.*;

public class RequestHandlerManagerTest {

    @Test
    public void test() {
        // given
        RequestHandlerManager sut = new RequestHandlerManager();
        RequestHandler requestHandler = mock(RequestHandler.class);
        RequestURI requestURI = new RequestURI(
            HttpMethod.GET,
            "/get", 
            RequestURIMeta.builder()
                .api(true)
                .authenticated(true)
                .management(true)
                .resource(true)
                .payment(true)
                .feature("hello.world")
                .resourceFullPath("/")
                .build()
        );
        sut.addHandler(requestURI, requestHandler);
        
        // when
        FeatureURIManager featureURIManager = sut.getFeatureURIManager();
        
        // then
        Asserts.assertEquals(featureURIManager.getFeatureByURI(HttpMethod.GET, "/get"), "hello.world");
        Asserts.assertEquals(featureURIManager.getFeatureByURI(HttpMethod.GET, "/get/"), "hello.world");
    }
}
