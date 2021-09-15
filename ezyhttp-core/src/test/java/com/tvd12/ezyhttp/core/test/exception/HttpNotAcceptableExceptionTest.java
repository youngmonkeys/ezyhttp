package com.tvd12.ezyhttp.core.test.exception;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpNotAcceptableException;
import com.tvd12.test.assertion.Asserts;

public class HttpNotAcceptableExceptionTest {

	@Test
	public void test() {
		// given
		int code = StatusCodes.NOT_ACCEPTABLE;
		String data = "error";
		
		// when
		HttpNotAcceptableException sut = new HttpNotAcceptableException(data);
		
		// then
		Asserts.assertEquals(code, sut.getCode());
		Asserts.assertEquals(data, sut.getData());
	}
}
