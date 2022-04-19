package com.tvd12.ezyhttp.client.test.concurrent;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.client.concurrent.RequestFutureTask;
import com.tvd12.test.assertion.Asserts;

public class RequestFutureTaskTest {

    @Test
    public void setResultNull() {
        // given
        RequestFutureTask sut = new RequestFutureTask();

        // when
        Throwable e = Asserts.assertThrows(() -> sut.setResult(null));

        // then
        Asserts.assertThat(e).isEqualsType(NullPointerException.class);
    }

    @Test
    public void setResultNotNull() throws Exception {
        // given
        RequestFutureTask sut = new RequestFutureTask();

        Object result = new Object();

        // when
        sut.setResult(result);

        // then
        Asserts.assertEquals(result, sut.get());
    }

    @Test
    public void setExceptionNull() {
        // given
        RequestFutureTask sut = new RequestFutureTask();

        // when
        Throwable e = Asserts.assertThrows(() -> sut.setException(null));

        // then
        Asserts.assertThat(e).isEqualsType(NullPointerException.class);
    }

    @Test
    public void setExceptionNotNull() {
        // given
        RequestFutureTask sut = new RequestFutureTask();

        Exception exception = new Exception("just test");

        // when
        sut.setException(exception);

        // then
        Throwable e = Asserts.assertThrows(() -> sut.get());
        Asserts.assertThat(e).isEqualsTo(exception);
    }
}
