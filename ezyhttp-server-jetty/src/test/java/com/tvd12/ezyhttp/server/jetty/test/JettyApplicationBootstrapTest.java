package com.tvd12.ezyhttp.server.jetty.test;

import com.tvd12.ezyhttp.server.jetty.JettyApplicationBootstrap;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

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
}
