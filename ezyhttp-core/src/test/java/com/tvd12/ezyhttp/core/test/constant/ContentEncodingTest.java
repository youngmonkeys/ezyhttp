package com.tvd12.ezyhttp.core.test.constant;

import com.tvd12.ezyhttp.core.constant.ContentEncoding;
import com.tvd12.ezyhttp.core.constant.ContentType;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class ContentEncodingTest {

    @Test
    public void ofMimeTypeTest() {
        Asserts.assertEquals(
            ContentEncoding.ofMimeType(
                ContentType.GZIP.getMimeType()
            ),
            ContentEncoding.GZIP
        );
        Asserts.assertNull(ContentEncoding.ofMimeType("not found"));
    }
    
    @Test
    public void getterTest() {
        Asserts.assertEquals(
            ContentEncoding.GZIP.getValue(),
            "gzip"
        );
        Asserts.assertEquals(
            ContentEncoding.GZIP.getMimeType(),
            ContentType.GZIP.getMimeType()
        );
    }

    @Test
    public void ofValueNullTest() {
        Asserts.assertNull(ContentEncoding.ofValue(null));
    }

    @Test
    public void offNonNullValueTest() {
        Asserts.assertEquals(
            ContentEncoding.ofValue("gzip"),
            ContentEncoding.GZIP
        );
    }
}
