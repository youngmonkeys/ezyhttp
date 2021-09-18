package com.tvd12.ezyhttp.core.test.exception;

import java.util.Map;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.exception.DeserializeHeaderException;
import com.tvd12.test.assertion.Asserts;

public class DeserializeHeaderExceptionTest {

	@Test
	public void test() {
		// given
		Exception e = new Exception("just test");
		
		// when
		DeserializeHeaderException sut = new DeserializeHeaderException(
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
