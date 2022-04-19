package com.tvd12.ezyhttp.core.test.util;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.util.BodyConvertAnnotations;
import com.tvd12.test.assertion.Asserts;

public class BodyConvertAnnotationsTest {

    @Test
    public void getContentTypeFailed() {
        // given
        Object conveter = getClass();

        // when
        Throwable e = Asserts.assertThrows(() -> BodyConvertAnnotations.getContentType(conveter));

        // then
        Asserts.assertThat(e).isEqualsType(IllegalArgumentException.class);
    }
}
