package com.tvd12.ezyhttp.core.test.exception;

import org.testng.annotations.Test;

import com.sun.javafx.collections.MappingChange.Map;
import com.tvd12.ezyhttp.core.exception.DeserializeCookieException;
import com.tvd12.test.assertion.Asserts;

public class DeserializeCookieExceptionTest {

	@Test
	public void test() {
		// given
		Exception e = new Exception("just test");
		
		// when
		DeserializeCookieException sut = new DeserializeCookieException(
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
	
}
