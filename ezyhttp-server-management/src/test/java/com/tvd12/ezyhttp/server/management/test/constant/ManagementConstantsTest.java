package com.tvd12.ezyhttp.server.management.test.constant;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.management.constant.ManagementConstants;
import com.tvd12.test.assertion.Asserts;

public class ManagementConstantsTest {

    @Test
    public void test() {
        // given
        // when
        // then
        Asserts.assertEquals(ManagementConstants.DEFAULT_FEATURE_NAME, "devops");
    }
}
