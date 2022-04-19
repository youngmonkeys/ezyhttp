package com.tvd12.ezyhttp.core.test.exception;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpMethodNotAllowedException;
import com.tvd12.test.assertion.Asserts;

public class HttpMethodNotAllowedExceptionTest {

    @Test
    public void test() {
        // given
        int code = StatusCodes.METHOD_NOT_ALLOWED;
        String data = "error";

        // when
        HttpMethodNotAllowedException sut = new HttpMethodNotAllowedException(data);

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
    }
}
