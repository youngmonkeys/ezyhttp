package com.tvd12.ezyhttp.server.core.test.asm;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.asm.RequestHandlersImplementer;
import com.tvd12.ezyhttp.server.core.exception.DuplicateURIMappingHandlerException;
import com.tvd12.test.assertion.Asserts;

public class RequestHandlersImplementerTest {

	@Test
	public void implementOneFailed() {
		// given
		RequestHandlersImplementer sut = new RequestHandlersImplementer();
		Controller controller = new Controller();
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.implement(controller));
		
		// then
		Asserts.assertThat(e).isEqualsType(DuplicateURIMappingHandlerException.class);
	}
	
	@Test
	public void implementMultiFailed() {
		// given
		RequestHandlersImplementer sut = new RequestHandlersImplementer();
		Controller2 controller2 = new Controller2();
		Controller3 controller3 = new Controller3();
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.implement(
				Arrays.asList(controller2, controller3
		)));
		
		// then
		Asserts.assertThat(e).isEqualsType(DuplicateURIMappingHandlerException.class);
	}
	
	public static class Controller {
		
		@DoGet("/get")
		public void doGet() {}
		
		@DoGet("/get")
		public void doGet1() {}
	}
	
	public static class Controller2 {
		
		@DoGet("/get")
		public void doGet() {}
	}
	
	public static class Controller3 {
		
		@DoGet("/get")
		public void doGet() {}
	}
}
