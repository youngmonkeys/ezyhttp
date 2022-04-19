package com.tvd12.ezyhttp.core.test.exception;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpUnsupportedMediaTypeException;
import com.tvd12.test.assertion.Asserts;

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
}
