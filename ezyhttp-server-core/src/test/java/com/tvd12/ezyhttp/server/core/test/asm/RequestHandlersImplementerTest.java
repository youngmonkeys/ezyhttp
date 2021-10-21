package com.tvd12.ezyhttp.server.core.test.asm;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.asm.RequestHandlersImplementer;
import com.tvd12.ezyhttp.server.core.exception.DuplicateURIMappingHandlerException;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.core.request.RequestURI;
import com.tvd12.test.assertion.Asserts;

public class RequestHandlersImplementerTest {

	@Test
	public void implementOneFailed() {
		// given
		RequestHandlersImplementer sut = new RequestHandlersImplementer();
		Controller controller = new Controller();
		RequestHandlerManager manager = new RequestHandlerManager();
        
        // when
        Throwable e = Asserts.assertThrows(() ->
            manager.addHandlers(sut.implement(
                    Arrays.asList(controller)
            ))
        );
		
		// then
		Asserts.assertThat(e).isEqualsType(DuplicateURIMappingHandlerException.class);
	}
	
	@Test
	public void implementMultiFailed() {
		// given
		RequestHandlersImplementer sut = new RequestHandlersImplementer();
		Controller2 controller2 = new Controller2();
		Controller3 controller3 = new Controller3();
		RequestHandlerManager manager = new RequestHandlerManager();
		
		// when
		Throwable e = Asserts.assertThrows(() ->
    		manager.addHandlers(sut.implement(
                    Arrays.asList(controller2, controller3)
            ))
		);
		
		// then
		Asserts.assertThat(e).isEqualsType(DuplicateURIMappingHandlerException.class);
	}
	
	@Test
    public void implementOneAllowOverrideURI() {
        // given
        RequestHandlersImplementer sut = new RequestHandlersImplementer();
        Controller controller = new Controller();
        RequestHandlerManager manager = new RequestHandlerManager();
        manager.setAllowOverrideURI(true);
        
        // when
        manager.addHandlers(sut.implement(Arrays.asList(controller)));
        
        // then
        RequestURI uri = new RequestURI(HttpMethod.GET, "/get", false);
        Asserts.assertThat(manager.getHandlerListByURI().get(uri).size()).isEqualsTo(2);
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
