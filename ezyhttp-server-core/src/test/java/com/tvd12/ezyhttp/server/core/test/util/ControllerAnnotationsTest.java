package com.tvd12.ezyhttp.server.core.test.util;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.util.ControllerAnnotations;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import static com.tvd12.ezyhttp.core.constant.Constants.DEFAULT_URI;

public class ControllerAnnotationsTest {

    @Test
    public void getURIWithNullAnnotation() {
        // given
        // when
        String actual = ControllerAnnotations.getURI((Controller) null);

        // then
        Asserts.assertEquals(actual, DEFAULT_URI);
    }
}
