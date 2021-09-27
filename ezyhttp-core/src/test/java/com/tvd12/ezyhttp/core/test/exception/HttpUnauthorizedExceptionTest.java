package com.tvd12.ezyhttp.core.test.exception;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpUnauthorizedException;
import com.tvd12.test.assertion.Asserts;

public class HttpUnauthorizedExceptionTest {

	@Test
	public void test() {
		// given
		int code = StatusCodes.UNAUTHORIZED;
		String data = "error";
		
		// when
		HttpUnauthorizedException sut = new HttpUnauthorizedException(data);
		
		// then
		Asserts.assertEquals(code, sut.getCode());
		Asserts.assertEquals(data, sut.getData());
	}
}
