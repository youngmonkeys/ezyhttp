package com.tvd12.ezyhttp.server.core.test.reflect;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.annotation.TryCatch;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerProxy;

public class ExceptionHandlerProxyTest {

	@Test
	public void test() {
		// given
		ExceptionHandler handler = new ExceptionHandler();
		ExceptionHandlerProxy sut = new ExceptionHandlerProxy(handler);
		
		// when
		// then
		System.out.println(sut);
	}
	
	private static class ExceptionHandler {
		
		@TryCatch(Exception.class)
		public void handleException(Exception e) {}
	}
}
