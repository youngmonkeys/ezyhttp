package com.tvd12.ezyhttp.core.test.exception;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpConflictException;
import com.tvd12.test.assertion.Asserts;

public class HttpConflictExceptionTest {

    @Test
    public void test() {
        // given
        int code = StatusCodes.CONFLICT;
        String data = "error";

        // when
        HttpConflictException sut = new HttpConflictException(data);

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
    }
}
