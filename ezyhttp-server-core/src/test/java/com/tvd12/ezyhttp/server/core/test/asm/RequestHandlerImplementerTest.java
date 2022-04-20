package com.tvd12.ezyhttp.server.core.test.asm;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.asm.RequestHandlerImplementer;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;
import com.tvd12.test.assertion.Asserts;

public class RequestHandlerImplementerTest {

    @Test
    public void implementOneFailed() throws Exception {
        // given
        ControllerProxy controller = new ControllerProxy(new Controller());
        RequestHandlerMethod handlerMethod = new RequestHandlerMethod(
            "/",
            new EzyMethod(Controller.class.getDeclaredMethod("doGet")));
        RequestHandlerImplementer sut = new RequestHandlerImplementer(
            controller,
            handlerMethod);

        // when
        Throwable e = Asserts.assertThrows(sut::implement);

        // then
        Asserts.assertEquals(0, handlerMethod.getParameterTypes().length);
        Asserts.assertThat(e).isEqualsType(IllegalStateException.class);
    }

    private static class Controller {

        @DoGet("/get")
        private void doGet() {}
    }
}
