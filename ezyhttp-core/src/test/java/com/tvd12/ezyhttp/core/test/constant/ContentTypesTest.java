package com.tvd12.ezyhttp.core.test.constant;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class ContentTypesTest {

    @Test
    public void getContentTypeWithNull() {
        // given
        // when
        // then
        Asserts.assertNull(ContentTypes.getContentType(null));
    }
}
