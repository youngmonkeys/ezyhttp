package com.tvd12.ezyhttp.core.test.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class HttpNotFoundExceptionTest {

    @Test
    public void test() {
        // given
        int code = StatusCodes.NOT_FOUND;
        String data = "error";

        // when
        HttpNotFoundException sut = new HttpNotFoundException(data);

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
    }

    @Test
    public void withCauseTest() {
        // given
        int code = StatusCodes.NOT_FOUND;
        String data = "error";
        Exception cause = new Exception("test");

        // when
        HttpNotFoundException sut = new HttpNotFoundException(
            data,
            cause
        );

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
        Asserts.assertEquals(sut.getCause(), cause);
    }
}
