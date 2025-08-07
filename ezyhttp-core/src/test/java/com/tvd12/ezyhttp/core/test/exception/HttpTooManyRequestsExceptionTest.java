package com.tvd12.ezyhttp.core.test.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpTooManyRequestsException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class HttpTooManyRequestsExceptionTest {

    @Test
    public void test() {
        // given
        int code = StatusCodes.TOO_MANY_REQUESTS;
        String data = "error";

        // when
        HttpTooManyRequestsException sut = new HttpTooManyRequestsException(data);

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
    }

    @Test
    public void withCauseTest() {
        // given
        int code = StatusCodes.TOO_MANY_REQUESTS;
        String data = "error";
        Exception cause = new Exception("test");

        // when
        HttpTooManyRequestsException sut = new HttpTooManyRequestsException(
            data,
            cause
        );

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
        Asserts.assertEquals(sut.getCause(), cause);
    }
}
