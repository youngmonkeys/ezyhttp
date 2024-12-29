package com.tvd12.ezyhttp.server.core.test.util;

import com.tvd12.ezyhttp.server.core.util.EzyConfigurationAfterApplicationReadyAnnotations;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.MethodInvoker;
import org.testng.annotations.Test;

public class EzyConfigurationAfterApplicationReadyAnnotationsTest {

    @Test
    public void getPriorityTest() {
        // given
        // when
        int priority = (int) MethodInvoker
            .create()
            .staticClass(EzyConfigurationAfterApplicationReadyAnnotations.class)
            .method("getPriority")
            .param(Object.class, this)
            .invoke();

        // then
        Asserts.assertZero(priority);
    }
}
