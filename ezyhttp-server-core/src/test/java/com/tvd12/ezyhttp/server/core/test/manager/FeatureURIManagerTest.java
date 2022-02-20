package com.tvd12.ezyhttp.server.core.test.manager;

import java.util.Arrays;
import java.util.Collections;

import org.testng.annotations.Test;
import org.testng.collections.Sets;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.core.manager.FeatureURIManager;
import com.tvd12.test.assertion.Asserts;

public class FeatureURIManagerTest {

    @Test
    public void test() {
        // given
        FeatureURIManager sut = new FeatureURIManager();
        sut.addFeatureURI("hello", "/a");
        sut.addFeatureURI("hello", "/b");
        sut.addFeatureURI("world", "/c");
        
        // when
        Asserts.assertEquals(
            sut.getFeatures(),
            Sets.newHashSet("hello", "world"),
            false
        );
        Asserts.assertEquals(sut.getFeatureByURI("/a"), "hello");
        Asserts.assertEquals(sut.getFeatureByURI("/c"), "world");
        Asserts.assertEquals(sut.getURIsByFeature("hello"), Arrays.asList("/a", "/b"), false);
        Asserts.assertEquals(
            sut.getFeatureByURIMap(),
            EzyMapBuilder.mapBuilder()
                .put("/a", "hello")
                .put("/b", "hello")
                .put("/c", "world")
                .build()
        );
        Asserts.assertEquals(
            sut.getURIsByFeatureMap(),
            EzyMapBuilder.mapBuilder()
                .put("hello", Arrays.asList("/a", "/b"))
                .put("world", Collections.singletonList("/c"))
                .build(),
            false
        );
    }
}
