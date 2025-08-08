package com.tvd12.ezyhttp.core.test.exception;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpPaymentRequiredException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class HttpPaymentRequiredExceptionTest {

    @Test
    public void test() {
        // given
        int code = StatusCodes.PAYMENT_REQUIRED;
        String data = "error";

        // when
        HttpPaymentRequiredException sut = new HttpPaymentRequiredException(data);

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
    }

    @Test
    public void withCauseTest() {
        // given
        int code = StatusCodes.PAYMENT_REQUIRED;
        String data = "error";
        Exception cause = new Exception("test");

        // when
        HttpPaymentRequiredException sut = new HttpPaymentRequiredException(
            data,
            cause
        );

        // then
        Asserts.assertEquals(code, sut.getCode());
        Asserts.assertEquals(data, sut.getData());
        Asserts.assertEquals(sut.getCause(), cause);
    }
}
