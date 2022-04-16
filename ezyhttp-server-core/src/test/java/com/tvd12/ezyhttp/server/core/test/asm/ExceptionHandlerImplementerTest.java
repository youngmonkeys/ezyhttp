package com.tvd12.ezyhttp.server.core.test.asm;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;
import com.tvd12.ezyhttp.server.core.asm.ExceptionHandlerImplementer;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerMethod;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerProxy;
import com.tvd12.test.assertion.Asserts;

public class ExceptionHandlerImplementerTest {

	@Test
	public void implementOneFailed() throws Exception {
		// given
		ExceptionHandlerProxy handler = new ExceptionHandlerProxy(new ExceptionHandler());
		ExceptionHandlerMethod handlerMethod = new ExceptionHandlerMethod(
				new EzyMethod(ExceptionHandler.class.getDeclaredMethod("handle", Exception.class)));
		ExceptionHandlerImplementer sut = new ExceptionHandlerImplementer(
				handler,
				handlerMethod);
		
		// when
		Throwable e = Asserts.assertThrows(sut::implement);
		
		// then
		Asserts.assertThat(e).isEqualsType(IllegalStateException.class);
	}
	
	private static class ExceptionHandler {
		
		@TryCatch(Exception.class)
		private void handle(Exception e) {}
	}
}
