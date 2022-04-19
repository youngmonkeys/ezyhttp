package com.tvd12.ezyhttp.core.test.exception;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpRequestTimeoutException;
import com.tvd12.test.assertion.Asserts;

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
}
