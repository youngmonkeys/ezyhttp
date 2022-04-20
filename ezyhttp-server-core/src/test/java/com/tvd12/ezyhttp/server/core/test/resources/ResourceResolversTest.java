package com.tvd12.ezyhttp.server.core.test.resources;

import static org.mockito.Mockito.*;

import org.testng.annotations.Test;

import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.*;

import com.tvd12.ezyfox.bean.EzyPropertyFetcher;
import com.tvd12.ezyhttp.server.core.resources.ResourceResolver;
import com.tvd12.ezyhttp.server.core.resources.ResourceResolvers;
import com.tvd12.test.assertion.Asserts;

public class ResourceResolversTest {

    @Test
    public void test() {
        // given
        EzyPropertyFetcher propertyFetcher = mock(EzyPropertyFetcher.class);
        when(
            propertyFetcher.getProperty(RESOURCE_ENABLE, boolean.class, false)
        ).thenReturn(true);
        when(
            propertyFetcher.getProperty(RESOURCE_LOCATIONS, String[].class)
        ).thenReturn(new String[]{"static"});

        when(
            propertyFetcher.getProperty(RESOURCE_PATTERN, String.class)
        ).thenReturn("static/.+");

        // when
        ResourceResolver resourceResolver = ResourceResolvers.createResourdeResolver(propertyFetcher);

        // then
        assert resourceResolver != null;
        Asserts.assertEquals(4, resourceResolver.getResources().size());
    }

    @Test
    public void testWithNullPattern() {
        // given
        EzyPropertyFetcher propertyFetcher = mock(EzyPropertyFetcher.class);
        when(
            propertyFetcher.getProperty(RESOURCE_ENABLE, boolean.class, false)
        ).thenReturn(true);
        when(
            propertyFetcher.getProperty(RESOURCE_LOCATIONS, String[].class)
        ).thenReturn(new String[]{"static"});

        // when
        ResourceResolver resourceResolver = ResourceResolvers.createResourdeResolver(propertyFetcher);

        // then
        assert resourceResolver != null;
        Asserts.assertEquals(4, resourceResolver.getResources().size());
    }
}
