package com.tvd12.ezyhttp.server.core.test.asm;

import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.annotation.Api;
import com.tvd12.ezyhttp.server.core.annotation.Authenticatable;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.asm.RequestHandlersImplementer;
import com.tvd12.ezyhttp.server.core.exception.DuplicateURIMappingHandlerException;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.handler.RequestURIDecorator;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.core.request.RequestURI;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

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
                Collections.singletonList(controller)
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
        manager.addHandlers(sut.implement(Collections.singletonList(controller)));

        // then
        RequestURI uri = new RequestURI(HttpMethod.GET, "/get", false);
        Asserts.assertThat(manager.getHandlerListByURI().get(uri).size()).isEqualsTo(2);
    }

    @Test
    public void implementOneWithURIDecorator() {
        // given
        RequestHandlersImplementer sut = new RequestHandlersImplementer();
        Controller controller = new Controller();
        RequestHandlerManager manager = new RequestHandlerManager();
        manager.setAllowOverrideURI(true);

        RequestURIDecorator requestURIDecorator = mock(RequestURIDecorator.class);
        when(requestURIDecorator.decorate(any(), any())).thenReturn("hello-world");
        sut.setRequestURIDecorator(requestURIDecorator);

        // when
        manager.addHandlers(sut.implement(Collections.singletonList(controller)));

        // then
        RequestURI uri = new RequestURI(HttpMethod.GET, "/hello-world", false);
        Asserts.assertThat(manager.getHandlerListByURI().get(uri).size()).isEqualsTo(2);
    }

    @Test
    public void implementOthersTest() {
        // given
        RequestHandlersImplementer sut = new RequestHandlersImplementer();
        Controller4 controller = new Controller4();

        // when
        Map<RequestURI, List<RequestHandler>> handlers = sut.implement(
            Collections.singletonList(controller)
        );

        // then
        Asserts.assertEquals(handlers.size(), 3);
        Asserts.assertEquals(
            handlers.keySet(),
            Sets.newHashSet(
                new RequestURI(HttpMethod.GET, "/get", false, false, true, null),
                new RequestURI(HttpMethod.GET, "/hello", false, false, true, null),
                new RequestURI(HttpMethod.GET, "/world", false, false, true, null)
            ),
            false
        );
    }

    @Test
    public void implementOthersWithURIDecorator() {
        // given
        RequestHandlersImplementer sut = new RequestHandlersImplementer();
        Controller4 controller = new Controller4();
        RequestHandlerManager manager = new RequestHandlerManager();
        manager.setAllowOverrideURI(true);

        RequestURIDecorator requestURIDecorator = mock(RequestURIDecorator.class);
        when(requestURIDecorator.decorate(any(), any())).thenReturn("hello-world");
        sut.setRequestURIDecorator(requestURIDecorator);

        // when
        Map<RequestURI, List<RequestHandler>> handlers = sut.implement(
            Collections.singletonList(controller)
        );

        // then
        Asserts.assertEquals(handlers.size(), 1);
        Asserts.assertEquals(
            handlers.keySet(),
            Sets.newHashSet(
                new RequestURI(HttpMethod.GET, "hello-world", false, false, true, null)
            ),
            false
        );
    }

    @Test
    public void implementWithAuthenticatableMethod() {
        // given
        RequestHandlersImplementer sut = new RequestHandlersImplementer();
        Controller5 controller = new Controller5();
        RequestHandlerManager manager = new RequestHandlerManager();
        manager.setAllowOverrideURI(true);

        RequestURIDecorator requestURIDecorator = mock(RequestURIDecorator.class);
        when(requestURIDecorator.decorate(any(), any())).thenReturn("hello-world");
        sut.setRequestURIDecorator(requestURIDecorator);

        // when
        Map<RequestURI, List<RequestHandler>> handlers = sut.implement(
            Collections.singletonList(controller)
        );

        // then
        Asserts.assertEquals(handlers.size(), 1);
        Asserts.assertEquals(
            handlers.keySet(),
            Sets.newHashSet(
                new RequestURI(HttpMethod.GET, "hello-world", false, false, true, null)
            ),
            false
        );
    }

    @Test
    public void implementWithAuthenticatableClass() {
        // given
        RequestHandlersImplementer sut = new RequestHandlersImplementer();
        Controller6 controller = new Controller6();
        RequestHandlerManager manager = new RequestHandlerManager();
        manager.setAllowOverrideURI(true);

        RequestURIDecorator requestURIDecorator = mock(RequestURIDecorator.class);
        when(requestURIDecorator.decorate(any(), any())).thenReturn("hello-world");
        sut.setRequestURIDecorator(requestURIDecorator);

        // when
        Map<RequestURI, List<RequestHandler>> handlers = sut.implement(
            Collections.singletonList(controller)
        );

        // then
        Asserts.assertEquals(handlers.size(), 1);
        Asserts.assertEquals(
            handlers.keySet(),
            Sets.newHashSet(
                new RequestURI(HttpMethod.GET, "hello-world", false, false, true, null)
            ),
            false
        );
    }

    public static class Controller {

        @DoGet("/get")
        public void doGet() {
        }

        @DoGet("/get")
        public void doGet1() {
        }
    }

    public static class Controller2 {

        @DoGet("/get")
        public void doGet() {
        }
    }

    public static class Controller3 {

        @DoGet("/get")
        public void doGet() {
        }
    }

    public static class Controller4 {

        @Api
        @DoGet(uri = "/get", otherUris = {"/hello", "/world"})
        public void doGet() {
        }
    }

    public static class Controller5 {

        @Authenticatable
        @DoGet(uri = "/get", otherUris = {"/hello", "/world"})
        public void doGet() {
        }
    }

    @Authenticatable
    public static class Controller6 {

        @DoGet(uri = "/get", otherUris = {"/hello", "/world"})
        public void doGet() {
        }
    }
}
