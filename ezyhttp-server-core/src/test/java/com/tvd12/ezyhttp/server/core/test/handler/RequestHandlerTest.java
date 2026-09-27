package com.tvd12.ezyhttp.server.core.test.handler;

import java.util.Collections;
import java.util.Set;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.test.assertion.Asserts;

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

    @Test
    public void getAcceptDefaultTest() {
        // given
        ExRequestHandler handler = new ExRequestHandler();

        // when
        Set<String> actual = handler.getAccept();

        // then
        Asserts.assertEquals(actual, Collections.emptySet());
    }

    @Test
    public void setAcceptDefaultDoNothingTest() {
        // given
        ExRequestHandler handler = new ExRequestHandler();
        Set<String> accept = Collections.singleton(ContentTypes.MULTIPART_FORM_DATA);

        // when
        handler.setAccept(accept);

        // then
        Asserts.assertEquals(handler.getAccept(), Collections.emptySet());
    }

    @Test
    public void setAcceptNullDefaultDoNothingTest() {
        // given
        ExRequestHandler handler = new ExRequestHandler();

        // when
        handler.setAccept(null);

        // then
        Asserts.assertEquals(handler.getAccept(), Collections.emptySet());
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
