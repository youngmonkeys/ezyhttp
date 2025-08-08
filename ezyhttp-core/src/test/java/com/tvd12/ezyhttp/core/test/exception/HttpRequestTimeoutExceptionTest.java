package com.tvd12.ezyhttp.core.test.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpRequestTimeoutException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class HttpRequestTimeoutExceptionTest {

    @Test
    public void test() {
        // given
        int code = StatusCodes.REQUEST_TIMEOUT;
        String data = "error";

        // when
        HttpRequestTimeoutException sut = new HttpRequestTimeoutException(data);

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
    }

    @Test
    public void withCauseTest() {
        // given
        int code = StatusCodes.REQUEST_TIMEOUT;
        String data = "error";
        Exception cause = new Exception("test");

        // when
        HttpRequestTimeoutException sut = new HttpRequestTimeoutException(
            data,
            cause
        );

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
        Asserts.assertEquals(sut.getCause(), cause);
    }
}
