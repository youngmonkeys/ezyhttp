package com.tvd12.ezyhttp.server.management.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.manager.FeatureURIManager;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.core.request.RequestURI;
import com.tvd12.ezyhttp.server.management.WebManagementController;
import com.tvd12.ezyhttp.server.management.data.ApiInformation;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.MethodUtil;

public class WebManagementControllerTest {

    @Test
    public void apiListGetTest() {
        // given
        FeatureURIManager featureURIManager = mock(FeatureURIManager.class);
        RequestHandlerManager requestHandlerManager = mock(RequestHandlerManager.class);

        RequestURI requestURI = new RequestURI(HttpMethod.GET, "/api/v1/hello", false);
        RequestHandler requestHandler = mock(RequestHandler.class);
        when(requestHandler.getHandlerMethod()).thenReturn(
            MethodUtil.getMethod("apiListGetTest", WebManagementControllerTest.class)
        );
        when(requestHandler.getAccept()).thenReturn(Collections.singleton("application/json"));
        when(requestHandlerManager.getHandlerListByURI()).thenReturn(
            Collections.singletonMap(requestURI, Collections.singletonList(requestHandler))
        );

        WebManagementController sut = new WebManagementController(
            featureURIManager,
            requestHandlerManager
        );

        // when
        List<ApiInformation> actual = sut.apiListGet();

        // then
        Asserts.assertEquals(actual.size(), 1);
        ApiInformation api = actual.get(0);
        Asserts.assertEquals(api.getUri(), "/api/v1/hello");
        Asserts.assertEquals(api.getMethod(), HttpMethod.GET);
        Asserts.assertEquals(api.getAccept(), Collections.singleton("application/json"));
        Asserts.assertEquals(api.getHandlers().size(), 1);
        Asserts.assertEquals(api.getHandlers().get(0).getName(), "apiListGetTest");

        verify(requestHandlerManager, times(1)).getHandlerListByURI();
        verifyNoMoreInteractions(requestHandlerManager);
        verifyNoMoreInteractions(featureURIManager);
    }

    @Test
    public void apiListGetEmptyTest() {
        // given
        FeatureURIManager featureURIManager = mock(FeatureURIManager.class);
        RequestHandlerManager requestHandlerManager = mock(RequestHandlerManager.class);
        when(requestHandlerManager.getHandlerListByURI()).thenReturn(Collections.emptyMap());

        WebManagementController sut = new WebManagementController(
            featureURIManager,
            requestHandlerManager
        );

        // when
        List<ApiInformation> actual = sut.apiListGet();

        // then
        Asserts.assertTrue(actual.isEmpty());

        verify(requestHandlerManager, times(1)).getHandlerListByURI();
        verifyNoMoreInteractions(requestHandlerManager);
        verifyNoMoreInteractions(featureURIManager);
    }

    @Test
    public void featuresGetTest() {
        // given
        FeatureURIManager featureURIManager = mock(FeatureURIManager.class);
        RequestHandlerManager requestHandlerManager = mock(RequestHandlerManager.class);
        Map<String, Map<String, List<HttpMethod>>> uriByFeature = Collections.singletonMap(
            "devops",
            Collections.singletonMap(
                "/management/apis",
                Arrays.asList(HttpMethod.GET, HttpMethod.POST)
            )
        );
        when(featureURIManager.getURIsByFeatureMap()).thenReturn(uriByFeature);

        WebManagementController sut = new WebManagementController(
            featureURIManager,
            requestHandlerManager
        );

        // when
        Map<String, Map<String, List<HttpMethod>>> actual = sut.featuresGet();

        // then
        Asserts.assertEquals(actual, uriByFeature);

        verify(featureURIManager, times(1)).getURIsByFeatureMap();
        verifyNoMoreInteractions(featureURIManager);
        verifyNoMoreInteractions(requestHandlerManager);
    }

    @Test
    public void featureNamesGetTest() {
        // given
        FeatureURIManager featureURIManager = mock(FeatureURIManager.class);
        RequestHandlerManager requestHandlerManager = mock(RequestHandlerManager.class);
        List<String> features = Arrays.asList("devops", "admin");
        when(featureURIManager.getFeatures()).thenReturn(features);

        WebManagementController sut = new WebManagementController(
            featureURIManager,
            requestHandlerManager
        );

        // when
        List<String> actual = sut.featureNamesGet();

        // then
        Asserts.assertEquals(actual, features);

        verify(featureURIManager, times(1)).getFeatures();
        verifyNoMoreInteractions(featureURIManager);
        verifyNoMoreInteractions(requestHandlerManager);
    }
}
