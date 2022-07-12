package com.tvd12.ezyhttp.server.tomcat.test;

import com.tvd12.ezyhttp.server.tomcat.TomcatApplicationBootstrap;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

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
}
