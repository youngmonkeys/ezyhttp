package com.tvd12.ezyhttp.core.test.exception;

import java.util.Map;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.exception.DeserializeValueException;
import com.tvd12.test.assertion.Asserts;

public class DeserializeValueExceptionTest {

    @Test
    public void test() {
        // given
        Exception e = new Exception("just test");
        
        // when
        DeserializeValueException sut = new DeserializeValueException(
                "hello",
                "world",
                Map.class,
                e
        );
        
        // then
        Asserts.assertEquals("hello", sut.getValueName());
        Asserts.assertEquals("world", sut.getValue());
        Asserts.assertEquals(Map.class, sut.getOutType());
        Asserts.assertEquals(e, sut.getCause());
    }
    
    @Test
    public void valueNull() {
        // given
        Exception e = new Exception("just test");
        
        // when
        DeserializeValueException sut = new DeserializeValueException(
                "hello",
                null,
                Map.class,
                e
        );
        
        // then
        Asserts.assertEquals("hello", sut.getValueName());
        Asserts.assertNull(sut.getValue());
        Asserts.assertEquals(Map.class, sut.getOutType());
        Asserts.assertEquals(e, sut.getCause());
    }
}
