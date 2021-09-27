package com.tvd12.ezyhttp.server.core.test.manager;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;
import com.tvd12.ezyhttp.server.core.manager.ExceptionHandlerManager;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.test.assertion.Asserts;

public class ExceptionHandlerManagerTest {

	@Test
	public void test() {
		// given
		ExceptionHandlerManager sut = new ExceptionHandlerManager();
		
		ExExceptionHandler exceptionHandler = new ExExceptionHandler();
		sut.addExceptionHandler(exceptionHandler);
		
		ExUncaughtExceptionHandler uncaughtExceptionHandler = new ExUncaughtExceptionHandler();
		sut.addUncaughtExceptionHandler(Exception.class, uncaughtExceptionHandler);
		
		// when
		// then
		Asserts.assertEquals(1, sut.getExceptionHandlerList().size());
		Asserts.assertEquals(uncaughtExceptionHandler, sut.getUncaughtExceptionHandler(Exception.class));
	}
	
	public static class ExExceptionHandler {}
	
	public static class ExUncaughtExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public Object handleException(RequestArguments arguments, Exception exception) throws Exception {
			return null;
		}
		
	}
}
