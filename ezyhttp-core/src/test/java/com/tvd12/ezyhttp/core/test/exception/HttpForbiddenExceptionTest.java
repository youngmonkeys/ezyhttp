package com.tvd12.ezyhttp.core.test.exception;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpForbiddenException;
import com.tvd12.test.assertion.Asserts;

public class HttpForbiddenExceptionTest {

	@Test
	public void test() {
		// given
		int code = StatusCodes.FORBIDDEN;
		String data = "error";
		
		// when
		HttpForbiddenException sut = new HttpForbiddenException(data);
		
		// then
		Asserts.assertEquals(code, sut.getCode());
		Asserts.assertEquals(data, sut.getData());
	}
}
