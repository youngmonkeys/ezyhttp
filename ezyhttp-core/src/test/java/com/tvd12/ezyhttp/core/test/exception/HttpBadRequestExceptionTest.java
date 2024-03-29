package com.tvd12.ezyhttp.core.test.exception;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;
import com.tvd12.test.assertion.Asserts;

public class HttpBadRequestExceptionTest {

    @Test
    public void test() {
        // given
        int code = StatusCodes.BAD_REQUEST;
        String data = "error";

        // when
        HttpBadRequestException sut = new HttpBadRequestException(data);

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
    }
}
