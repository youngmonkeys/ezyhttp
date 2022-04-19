package com.tvd12.ezyhttp.core.test.exception;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.exception.DeserializeBodyException;
import com.tvd12.test.assertion.Asserts;

public class DeserializeBodyExceptionTest {

    @Test
    public void test() {
        // given
        Exception e = new Exception("just test");
        
        // when
        DeserializeBodyException sut = new DeserializeBodyException(
                "hello",
                e
        );
        
        // then
        Asserts.assertEquals("hello", sut.getMessage());
        Asserts.assertEquals(e, sut.getCause());
    }
    
}
