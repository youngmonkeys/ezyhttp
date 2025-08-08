package com.tvd12.ezyhttp.core.test.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpMethodNotAllowedException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

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

    @Test
    public void withCauseTest() {
        // given
        int code = StatusCodes.METHOD_NOT_ALLOWED;
        String data = "error";
        Exception cause = new Exception("test");

        // when
        HttpMethodNotAllowedException sut = new HttpMethodNotAllowedException(
            data,
            cause
        );

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
        Asserts.assertEquals(sut.getCause(), cause);
    }
}
