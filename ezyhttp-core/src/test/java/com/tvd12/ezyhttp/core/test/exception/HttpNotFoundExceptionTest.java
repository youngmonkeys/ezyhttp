package com.tvd12.ezyhttp.core.test.exception;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import com.tvd12.test.assertion.Asserts;

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
}
