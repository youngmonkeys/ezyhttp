package com.tvd12.ezyhttp.server.core.test.handler;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.handler.EmptyRequestHandler;
import com.tvd12.test.assertion.Asserts;

@Test
public class EmptyRequestHandlerTest {

	@Test
	public void test() {
		// given
		EmptyRequestHandler sut = EmptyRequestHandler.getInstance();
		sut.setController(new Object());
		sut.setHandlerMethod(null);
		
		// when
		// then
		Asserts.assertNull(sut.handle(null));
		Asserts.assertNull(sut.getHandlerMethod());
		Asserts.assertNull(sut.getMethod());
		Asserts.assertNull(sut.getRequestURI());
		Asserts.assertNull(sut.getResponseContentType());
	}
}
