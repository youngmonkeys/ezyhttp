package com.tvd12.ezyhttp.server.tomcat.test;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.tomcat.TomcatApplicationBootstrap;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;

public class TomcatApplicationBootstrapTest {

    @Test
    public void test() {
        // given
        TomcatApplicationBootstrap sut = new TomcatApplicationBootstrap();
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
        TomcatApplicationBootstrap sut = new TomcatApplicationBootstrap();

        // when
        // then
        sut.stop();
    }

    @Test
    public void compressionDisableTest() throws Exception {
        // given
        TomcatApplicationBootstrap sut = new TomcatApplicationBootstrap();
        sut.setCompressionEnable(false);
        sut.setCompressionMinSize("1MB");

        // when
        sut.start();

        // then
        sut.stop();
    }
}
