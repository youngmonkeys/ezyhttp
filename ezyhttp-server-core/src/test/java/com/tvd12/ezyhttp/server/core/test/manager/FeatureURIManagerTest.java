package com.tvd12.ezyhttp.server.core.test.manager;

import java.util.Collections;

import org.testng.annotations.Test;
import org.testng.collections.Sets;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.manager.FeatureURIManager;
import com.tvd12.test.assertion.Asserts;

public class FeatureURIManagerTest {

    @Test
    public void test() {
        // given
        FeatureURIManager sut = new FeatureURIManager();
        sut.addFeatureURI("hello", HttpMethod.GET, "/a");
        sut.addFeatureURI("hello", HttpMethod.POST, "/a");
        sut.addFeatureURI("hello", HttpMethod.POST, "/b");
        sut.addFeatureURI("world", HttpMethod.PUT, "/c");
        
        // when
        Asserts.assertEquals(
            sut.getFeatures(),
            Sets.newHashSet("hello", "world"),
            false
        );
        Asserts.assertEquals(sut.getFeatureByURI(HttpMethod.GET, "/a"), "hello");
        Asserts.assertEquals(sut.getFeatureByURI(HttpMethod.PUT, "/c"), "world");
        Asserts.assertNull(sut.getFeatureByURI(HttpMethod.TRACE, "I don't know"));
        
        Asserts.assertEquals(
            sut.getURIsByFeature("hello"),
            EzyMapBuilder.mapBuilder()
                .put("/a", Sets.newHashSet(HttpMethod.GET, HttpMethod.POST))
                .put("/b", Collections.singletonList(HttpMethod.POST))
                .build(),
            false
        );
        Asserts.assertEquals(
            sut.getFeatureByURIMap(),
            EzyMapBuilder.mapBuilder()
                .put(
                    "/a",
                    EzyMapBuilder.mapBuilder()
                        .put(HttpMethod.GET, "hello")
                        .put(HttpMethod.POST, "hello")
                        .build()
                )
                .put(
                    "/b",
                    EzyMapBuilder.mapBuilder()
                        .put(HttpMethod.POST, "hello")
                        .build()
                )
                .put(
                    "/c",
                    EzyMapBuilder.mapBuilder()
                        .put(HttpMethod.PUT, "world")
                        .build()
                )
                .build()
        );
        Asserts.assertEquals(
            sut.getURIsByFeatureMap(),
            EzyMapBuilder.mapBuilder()
                .put(
                    "hello",
                    EzyMapBuilder.mapBuilder()
                        .put("/a", Sets.newHashSet(HttpMethod.GET, HttpMethod.POST))
                        .put("/b", Sets.newHashSet(HttpMethod.POST))
                        .build()
                )
                .put(
                    "world",
                    EzyMapBuilder.mapBuilder()
                        .put("/c", Sets.newHashSet(HttpMethod.PUT))
                        .build()
                )
                .build(),
            false
        );
    }
}
