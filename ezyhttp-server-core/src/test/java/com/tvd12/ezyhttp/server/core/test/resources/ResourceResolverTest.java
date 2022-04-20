package com.tvd12.ezyhttp.server.core.test.resources;

import java.util.Map;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.resources.Resource;
import com.tvd12.ezyhttp.server.core.resources.ResourceResolver;
import com.tvd12.test.assertion.Asserts;

public class ResourceResolverTest {

    @Test
    public void test() {
        // given
        ResourceResolver sut = new ResourceResolver();
        sut.register("static/css");
        sut.register(new String[]{"templates/js"});

        // when
        Map<String, Resource> resources = sut.getResources();

        // then
        Asserts.assertEquals(3, resources.size());
    }
}
