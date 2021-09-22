package com.tvd12.ezyhttp.server.core.test.reflect;

import java.util.List;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerMethod;
import com.tvd12.ezyhttp.server.core.test.controller.HomeController;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;

public class ControllerProxyTest extends BaseTest {

	@Test
	public void test() {
		// given
		HomeController homeController = new HomeController();
		ControllerProxy sut = new ControllerProxy(homeController);
		
		// when
		List<ExceptionHandlerMethod> exceptionHandlerMethods = sut.getExceptionHandlerMethods();
		
		// then
		Asserts.assertEquals(2, exceptionHandlerMethods.size());
		System.out.println(sut);
	}
	
}
