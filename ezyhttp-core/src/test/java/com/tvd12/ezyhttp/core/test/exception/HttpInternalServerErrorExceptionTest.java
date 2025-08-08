package com.tvd12.ezyhttp.core.test.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpInternalServerErrorException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class HttpInternalServerErrorExceptionTest {

    @Test
    public void test() {
        // given
        int code = StatusCodes.INTERNAL_SERVER_ERROR;
        String data = "error";

        // when
        HttpInternalServerErrorException sut = new HttpInternalServerErrorException(data);

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
    }

    @Test
    public void withCauseTest() {
        // given
        int code = StatusCodes.INTERNAL_SERVER_ERROR;
        String data = "error";
        Exception cause = new Exception("test");

        // when
        HttpInternalServerErrorException sut = new HttpInternalServerErrorException(
            data,
            cause
        );

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
        Asserts.assertEquals(sut.getCause(), cause);
    }
}
