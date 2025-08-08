package com.tvd12.ezyhttp.core.test.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpUnsupportedMediaTypeException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class HttpUnsupportedMediaTypeExceptionTest {

    @Test
    public void test() {
        // given
        int code = StatusCodes.UNSUPPORTED_MEDIA_TYPE;
        String data = "error";

        // when
        HttpUnsupportedMediaTypeException sut = new HttpUnsupportedMediaTypeException(data);

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
    }

    @Test
    public void withCauseTest() {
        // given
        int code = StatusCodes.UNSUPPORTED_MEDIA_TYPE;
        String data = "error";
        Exception cause = new Exception("test");

        // when
        HttpUnsupportedMediaTypeException sut = new HttpUnsupportedMediaTypeException(
            data,
            cause
        );

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
        Asserts.assertEquals(sut.getCause(), cause);
    }
}
