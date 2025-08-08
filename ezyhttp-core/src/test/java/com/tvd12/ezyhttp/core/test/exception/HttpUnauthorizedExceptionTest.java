package com.tvd12.ezyhttp.core.test.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpUnauthorizedException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class HttpUnauthorizedExceptionTest {

    @Test
    public void test() {
        // given
        int code = StatusCodes.UNAUTHORIZED;
        String data = "error";

        // when
        HttpUnauthorizedException sut = new HttpUnauthorizedException(data);

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
    }

    @Test
    public void withCauseTest() {
        // given
        int code = StatusCodes.UNAUTHORIZED;
        String data = "error";
        Exception cause = new Exception("test");

        // when
        HttpUnauthorizedException sut = new HttpUnauthorizedException(
            data,
            cause
        );

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
        Asserts.assertEquals(sut.getCause(), cause);
    }
}
