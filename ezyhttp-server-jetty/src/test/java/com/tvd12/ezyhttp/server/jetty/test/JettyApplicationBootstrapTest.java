package com.tvd12.ezyhttp.server.jetty.test;

import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.testng.annotations.Test;

import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyhttp.server.jetty.JettyApplicationBootstrap;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.reflect.MethodInvoker;
import com.tvd12.test.util.RandomUtil;

public class JettyApplicationBootstrapTest {

    @Test
    public void test() {
        // given
        JettyApplicationBootstrap sut = new JettyApplicationBootstrap();
        String headers = RandomUtil.randomShortAlphabetString();
        sut.setAllowedHeaders(headers);

        // when
        String allowedHeaders = FieldUtil.getFieldValue(sut, "allowedHeaders");

        // then
        Asserts.assertEquals(allowedHeaders, headers);
    }

    @Test
    public void stopBeforeStartTest() {
        // given
        JettyApplicationBootstrap instance = new JettyApplicationBootstrap();
        instance.setCompressionEnable(false);

        // when
        // then
        instance.stop();
    }

    @Test
    public void compressionDisableTest() throws Exception {
        // given
        JettyApplicationBootstrap instance = new JettyApplicationBootstrap();
        instance.setCompressionEnable(false);

        // when
        instance.start();

        // then
        instance.stop();
    }

    @Test
    public void newGzipHandlerTest() {
        // given
        JettyApplicationBootstrap instance = new JettyApplicationBootstrap();
        int minSize = RandomUtil.randomInt(50, 100);
        instance.setCompressionMinSize(minSize + "B");

        String[] includedMethods = new String[] { "GET", "POST" };
        instance.setCompressionIncludedMethods(includedMethods);

        String[] excludedMethods = new String[] { "DELETE", "PUT" };
        instance.setCompressionExcludedMethods(excludedMethods);

        String[] includedMimeTypes = new String[] {
            "text/html",
            "text/css",
            "text/javascript"
        };
        instance.setCompressionIncludedMimeTypes(includedMimeTypes);

        String[] excludedMimeTypes= new String[] { "image/tiff", "image/png" };
        instance.setCompressionExcludedMimeTypes(excludedMimeTypes);

        // when
        GzipHandler gzipHandler = MethodInvoker
            .create()
            .object(instance)
            .method("newGzipHandler")
            .invoke(GzipHandler.class);

        // then
        Asserts.assertEquals(gzipHandler.getMinGzipSize(), minSize);
        Asserts.assertEquals(
            Sets.newHashSet(gzipHandler.getIncludedMethods()),
            Sets.newHashSet(includedMethods)
        );
        Asserts.assertEquals(
            Sets.newHashSet(gzipHandler.getExcludedMethods()),
            Sets.newHashSet(excludedMethods)
        );
        Asserts.assertEquals(
            Sets.newHashSet(gzipHandler.getIncludedMimeTypes()),
            Sets.newHashSet(includedMimeTypes)
        );
        Asserts.assertEquals(
            Sets.newHashSet(gzipHandler.getExcludedMimeTypes()),
            Sets.newHashSet(excludedMimeTypes)
        );
    }
}
