package com.tvd12.ezyhttp.server.core.test.interceptor;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;

public class RequestInterceptorTest {

	@Test
	public void test() throws Exception {
		// given
		RequestInterceptor sut = new RequestInterceptor() {};
		
		
		// when
		// then
		sut.preHandle(null, null);
		sut.postHandle(null, null);
	}
}
