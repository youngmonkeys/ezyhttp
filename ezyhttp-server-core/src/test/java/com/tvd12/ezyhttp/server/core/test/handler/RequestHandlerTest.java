package com.tvd12.ezyhttp.server.core.test.handler;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

public class RequestHandlerTest {

	@Test
	public void test() {
		// given
		ExRequestHandler handler = new ExRequestHandler();
		
		// when
		// then
		handler.setController(null);
		handler.setHandlerMethod(null);
	}
	
	private static class ExRequestHandler implements RequestHandler {

		@Override
		public Object handle(RequestArguments arguments) {
			return null;
		}

		@Override
		public HttpMethod getMethod() {
			return null;
		}

		@Override
		public String getRequestURI() {
			return null;
		}

		@Override
		public String getResponseContentType() {
			return null;
		}
		
	}
}
