package com.tvd12.ezyhttp.server.core.test.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.security.EzyBase64;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.constant.CoreConstants;
import com.tvd12.ezyhttp.server.core.request.SimpleRequestArguments;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import javax.servlet.AsyncContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class SimpleRequestArgumentsTest {

    @Test
    public void test() throws Exception {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        sut.setMethod(HttpMethod.HEAD);
        sut.setArgument("arg1", "argValue1");
        sut.setUriTemplate("/");

        ServletInputStream inputStream = mock(ServletInputStream.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getInputStream()).thenReturn(inputStream);
        when(servletRequest.getRequestURI()).thenReturn("/");
        when(servletRequest.getContentType()).thenReturn(ContentTypes.TEXT_HTML_UTF8);
        sut.setRequest(servletRequest);

        AsyncContext asyncContext = mock(AsyncContext.class);
        when(servletRequest.getAsyncContext()).thenReturn(asyncContext);

        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        sut.setResponse(servletResponse);

        // when
        String arg1 = sut.getArgument("arg1");

        // then
        Asserts.assertEquals("argValue1", arg1);
        Asserts.assertNull(sut.getArgument("unknown"));
        Asserts.assertNull(sut.getParameter(0));
        Asserts.assertNull(sut.getParameter("unknown"));
        Asserts.assertNull(sut.getHeader(0));
        Asserts.assertNull(sut.getHeader("unknown"));
        Asserts.assertNull(sut.getPathVariable(0));
        Asserts.assertNull(sut.getPathVariable("unknown"));
        Asserts.assertEquals(0, sut.getContentLength());
        Asserts.assertEquals(ContentTypes.TEXT_HTML, sut.getContentType());
        Asserts.assertEquals(ContentTypes.TEXT_HTML_UTF8, sut.getRequestContentType());
        Asserts.assertEquals(inputStream, sut.getInputStream());
        Asserts.assertNull(sut.getCookie("unknown"));
        Asserts.assertNull(sut.getCookieValue(0));
        Asserts.assertNull(sut.getCookieValue("unknown"));
        Asserts.assertEquals(HttpMethod.HEAD, sut.getMethod());
        Asserts.assertEquals(sut.getAsyncContext(), asyncContext);
        Asserts.assertFalse(sut.isAsyncStarted());
        Asserts.assertEquals(sut.getUriTemplate(), "/");

        verify(servletRequest, times(1)).getRequestURI();
        verify(servletRequest, times(1)).getInputStream();
        sut.release();
    }

    @Test
    public void argumentTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();

        // when
        // then
        Asserts.assertNull(sut.getArgument("unknown"));
        sut.release();
    }

    @Test
    public void argumentButDebugTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        sut.setDebug(true);

        // when
        Object actual = sut.getArgument("unknown");

        // then
        Asserts.assertNull(actual);
        sut.release();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void parameterTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterNames()).thenReturn(
            Collections.enumeration(Arrays.asList("hello", "foo"))
        );
        when(request.getParameterValues("hello")).thenReturn(new String[]{"world"});
        when(request.getParameterValues("foo")).thenReturn(new String[]{"bar"});
        sut.setRequest(request);

        // when
        String param0 = sut.getParameter(0);
        String param1 = sut.getParameter(1);
        String paramOverSize = sut.getParameter(3);
        String paramByName = sut.getParameter("hello");
        Map<String, String> params = sut.getParameters();

        // then
        Asserts.assertEquals("world", param0);
        Asserts.assertEquals("bar", param1);
        Asserts.assertNull(paramOverSize);
        Asserts.assertEquals("world", paramByName);
        verify(request, times(1)).getParameterNames();

        Map<String, String> expectedParams = EzyMapBuilder.mapBuilder()
            .put("hello", "world")
            .put("foo", "bar")
            .build();
        Asserts.assertEquals(expectedParams, params);
        sut.release();
    }

    @Test
    public void headerTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(
            Collections.enumeration(Arrays.asList("hello", "foo"))
        );
        when(request.getHeader("hello")).thenReturn("world");
        when(request.getHeader("foo")).thenReturn("bar");
        sut.setRequest(request);

        // when
        String header0 = sut.getHeader(0);
        String header1 = sut.getHeader(1);
        String headerOverSize = sut.getHeader(3);
        String headerByName = sut.getHeader("hello");

        // then
        Asserts.assertEquals("world", header0);
        Asserts.assertEquals("bar", header1);
        Asserts.assertNull(headerOverSize);
        Asserts.assertEquals("world", headerByName);
        Asserts.assertEquals(
            sut.getHeaders(),
            EzyMapBuilder.mapBuilder()
                .put("hello", "world")
                .put("foo", "bar")
                .toMap(),
            false
        );
        verify(request, times(1)).getHeaderNames();
        sut.release();
    }

    @Test
    public void headerWithoutRequestTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();

        // when
        // then
        Asserts.assertNull(sut.getHeader(0));
        Asserts.assertNull(sut.getHeader("hello"));
        Asserts.assertNull(sut.getHeaders());
        sut.release();
    }

    @Test
    public void headerNamesNullTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        sut.setRequest(request);

        // when
        // then
        Asserts.assertNull(sut.getHeader(0));
        Asserts.assertNull(sut.getHeaders());
        verify(request, times(1)).getHeaderNames();
        sut.release();
    }

    @Test
    public void pathVariableTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        sut.setUriTemplate("/{foo}/{bar}");

        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getRequestURI()).thenReturn("/hello/world");
        sut.setRequest(servletRequest);

        // when
        String pathVariableByIndex = sut.getPathVariable(1);
        String pathVariableOverIndex = sut.getPathVariable(3);
        String pathVariableByName = sut.getPathVariable("foo");

        // then
        Asserts.assertNull(pathVariableOverIndex);
        Asserts.assertEquals("hello", pathVariableByName);
        Asserts.assertEquals("world", pathVariableByIndex);
        verify(servletRequest, times(1)).getRequestURI();
        sut.release();
    }

    @Test
    public void cookieEmptyTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        sut.setCookies(new Cookie[0]);

        // when
        // then
        Asserts.assertNull(sut.getCookieValue(0));
        Asserts.assertNull(sut.getCookieValue(1));
        Asserts.assertNull(sut.getCookieValue("unknown"));
        Asserts.assertNull(sut.getCookie("unknown"));
        sut.release();
    }

    @Test
    public void cookieNullTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        sut.setCookies(null);

        // when
        // then
        Asserts.assertNull(sut.getCookieValue(0));
        Asserts.assertNull(sut.getCookieValue(1));
        Asserts.assertNull(sut.getCookieValue("unknown"));
        Asserts.assertNull(sut.getCookie("unknown"));
        sut.release();
    }

    @Test
    public void cookieTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        sut.setCookies(
            new Cookie[]{
                new Cookie("hello", ""),
                new Cookie("hello", "world"),
                new Cookie("hello", "")
            }
        );

        // when
        // then
        Asserts.assertNull(sut.getCookieValue(3));
        Asserts.assertEquals("world", sut.getCookieValue(0));
        Asserts.assertEquals("world", sut.getCookie("hello").getValue());
        Asserts.assertEquals("world", sut.getCookieValue("hello"));
        Asserts.assertNull(sut.getCookie("unknown"));
        sut.release();
    }

    @Test
    public void getByDefaultTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();

        // when
        // then
        Asserts.assertEquals("paramValue", sut.getParameter(1, "paramValue"));
        Asserts.assertEquals("paramValue", sut.getParameter("key", "paramValue"));
        Asserts.assertEquals("headerValue", sut.getHeader(1, "headerValue"));
        Asserts.assertEquals("headerValue", sut.getHeader("key", "headerValue"));
        Asserts.assertEquals("cookieValue", sut.getCookieValue(1, "cookieValue"));
        Asserts.assertEquals("cookieValue", sut.getCookieValue("key", "cookieValue"));
        sut.release();
    }

    @Test
    public void getByDefaultButNotNullTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(
            Collections.enumeration(Collections.singletonList("key"))
        );
        when(request.getHeader("key")).thenReturn("headerValue0");
        when(request.getParameterNames()).thenReturn(
            Collections.enumeration(Collections.singletonList("key"))
        );
        when(request.getParameterValues("key")).thenReturn(new String[]{"paramValue0"});
        sut.setRequest(request);
        sut.setCookies(new Cookie[]{new Cookie("key", "cookieValue0")});

        // when
        // then
        Asserts.assertEquals("paramValue0", sut.getParameter(0, "paramValue"));
        Asserts.assertEquals("paramValue0", sut.getParameter("key", "paramValue"));
        Asserts.assertEquals("headerValue0", sut.getHeader(0, "headerValue"));
        Asserts.assertEquals("headerValue0", sut.getHeader("key", "headerValue"));
        Asserts.assertEquals("cookieValue0", sut.getCookieValue(0, "cookieValue"));
        Asserts.assertEquals("cookieValue0", sut.getCookieValue("key", "cookieValue"));
        sut.release();
    }

    @Test
    public void parameterWithoutRequestTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();

        // when
        // then
        Asserts.assertNull(sut.getParameter(0));
        Asserts.assertNull(sut.getParameter("hello"));
        Asserts.assertNull(sut.getParameters());
        sut.release();
    }

    @Test
    public void parameterEmptyTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterNames()).thenReturn(
            Collections.enumeration(Collections.singletonList("key"))
        );
        when(request.getParameterValues("key")).thenReturn(new String[0]);
        sut.setRequest(request);

        // when
        // then
        Asserts.assertEquals("", sut.getParameter(0, "paramValue"));
        Asserts.assertEquals("", sut.getParameter("key", "paramValue"));
        sut.release();
    }

    @Test
    public void parameterMultiTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterNames()).thenReturn(
            Collections.enumeration(Collections.singletonList("key"))
        );
        when(request.getParameterValues("key")).thenReturn(new String[]{"a", "b"});
        sut.setRequest(request);

        // when
        // then
        Asserts.assertEquals("a,b", sut.getParameter(0, "paramValue"));
        Asserts.assertEquals("a,b", sut.getParameter("key", "paramValue"));
        sut.release();
    }

    @Test
    public void setRedirectionAttributesFromCookieTest() throws IOException {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleRequestArguments sut = new SimpleRequestArguments();
        sut.setObjectMapper(objectMapper);
        sut.setResponse(mock(HttpServletResponse.class));

        Map<String, Map<String, Object>> data = new HashMap<>();
        data.put("hello", Collections.singletonMap("foo", true));
        data.put("world", Collections.singletonMap("bar", 10));

        String dataString = EzyBase64.encodeUtf(
            objectMapper.writeValueAsString(data)
        );

        Cookie cookie = new Cookie(
            CoreConstants.COOKIE_REDIRECT_ATTRIBUTES_NAME,
            dataString
        );
        sut.setCookies(new Cookie[]{cookie});

        // when
        sut.setRedirectionAttributesFromCookie();

        // then
        Map<String, Object> actualAttributes = sut.getRedirectionAttributes();
        Asserts.assertEquals(actualAttributes, data, false);
        Asserts.assertEquals(
            sut.getRedirectionAttribute("hello"),
            data.get("hello"), false
        );
        Asserts.assertEquals(
            sut.getRedirectionAttribute("world", Map.class),
            data.get("world"),
            false
        );
        Asserts.assertEquals(
            sut.getRedirectionAttribute("world", Map.class, Collections.emptyMap()),
            data.get("world"),
            false
        );
        Asserts.assertEquals(
            sut.getRedirectionAttribute("hello", "world"),
            data.get("hello"),
            false
        );
        Asserts.assertEquals(
            sut.getRedirectionAttribute("hello"),
            data.get("hello"),
            false
        );
        Asserts.assertEquals(
            sut.getRedirectionAttribute("not found", true),
            true
        );
        Asserts.assertEquals(
            sut.getRedirectionAttribute("not found", int.class, 0),
            0
        );
        sut.release();
    }

    @Test
    public void setRedirectionAttributesFromCookieExceptionTest() {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleRequestArguments sut = new SimpleRequestArguments();
        sut.setObjectMapper(objectMapper);
        sut.setResponse(mock(HttpServletResponse.class));

        String dataString = EzyBase64.encodeUtf("hello world");

        Cookie cookie = new Cookie(
            CoreConstants.COOKIE_REDIRECT_ATTRIBUTES_NAME,
            dataString
        );
        sut.setCookies(new Cookie[]{cookie});

        // when
        sut.setRedirectionAttributesFromCookie();

        // then
        Map<String, Object> actualAttributes = sut.getRedirectionAttributes();
        Asserts.assertNull(actualAttributes);
        Asserts.assertNull(sut.getRedirectionAttribute("hello"));
        Asserts.assertNull(sut.getRedirectionAttribute("hello", Map.class));
    }

    @Test
    public void getRequestValueAnywayCaseInAttributeTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/hello/world");
        when(request.getAttribute("hello")).thenReturn("world");
        sut.setRequest(request);
        sut.setUriTemplate("/hello/hello");

        // when
        String actual1 = sut.getRequestValueAnyway("hello");
        String actual2 = sut.getRequestValueAnyway("worlD");

        // then
        Asserts.assertEquals(actual1, "world");
        Asserts.assertEquals(actual2, null);

        verify(request, times(1)).getAttribute("hello");
        verify(request, times(1)).getAttribute("worlD");
        verify(request, times(1)).getAttribute("world");
        verify(request, times(1)).getAttribute("World");
        verify(request, times(1)).getAttribute("hello");
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getHeader("hello");
        verify(request, times(1)).getHeader("worlD");
        verify(request, times(1)).getHeader("world");
        verify(request, times(1)).getHeader("World");
        verify(request, times(1)).getParameterValues("hello");
        verify(request, times(1)).getParameterValues("worlD");
        verify(request, times(1)).getParameterValues("world");
        verify(request, times(1)).getParameterValues("World");
        verifyNoMoreInteractions(request);
    }

    @Test
    public void getRequestValueAnywayCaseInAttribute2Test() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/hello/world");
        when(request.getAttribute("hello")).thenReturn("world");
        sut.setRequest(request);
        sut.setUriTemplate("/hello/hello");

        // when
        String actual = sut.getRequestValueAnyway("worlD", "hello");

        // then
        Asserts.assertEquals(actual, "world");

        verify(request, times(1)).getAttribute("hello");
        verify(request, times(1)).getAttribute("worlD");
        verify(request, times(1)).getAttribute("world");
        verify(request, times(1)).getAttribute("World");
        verify(request, times(1)).getAttribute("hello");
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getHeader("hello");
        verify(request, times(1)).getHeader("worlD");
        verify(request, times(1)).getHeader("world");
        verify(request, times(1)).getHeader("World");
        verify(request, times(1)).getParameterValues("hello");
        verify(request, times(1)).getParameterValues("worlD");
        verify(request, times(1)).getParameterValues("world");
        verify(request, times(1)).getParameterValues("World");
        verifyNoMoreInteractions(request);
    }

    @Test
    public void getRequestValueAnywayCaseInAttributeEmtpyTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/hello/world");
        when(request.getAttribute("hello")).thenReturn("world");
        sut.setRequest(request);
        sut.setUriTemplate("/hello/hello");

        // when
        String actual = sut.getRequestValueAnyway();

        // then
        Asserts.assertNull(actual);
        verifyNoMoreInteractions(request);
    }

    @Test
    public void getRequestValueAnywayCaseInPathVariableTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/hello/world");
        sut.setRequest(request);
        sut.setUriTemplate("/hello/{hello}");

        // when
        String actual1 = sut.getRequestValueAnyway("hello");
        String actual2 = sut.getRequestValueAnyway("worlD");

        // then
        Asserts.assertEquals(actual1, "world");
        Asserts.assertEquals(actual2, null);

        verify(request, times(0)).getAttribute("hello");
        verify(request, times(1)).getAttribute("worlD");
        verify(request, times(1)).getAttribute("world");
        verify(request, times(1)).getAttribute("World");
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getHeader("hello");
        verify(request, times(1)).getHeader("worlD");
        verify(request, times(1)).getHeader("world");
        verify(request, times(1)).getHeader("World");
        verify(request, times(1)).getParameterValues("hello");
        verify(request, times(1)).getParameterValues("worlD");
        verify(request, times(1)).getParameterValues("world");
        verify(request, times(1)).getParameterValues("World");
        verifyNoMoreInteractions(request);
    }

    @Test
    public void getRequestValueAnywayCaseInHeaderTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/hello/world");
        when(request.getHeader("hello")).thenReturn("world");
        sut.setRequest(request);
        sut.setUriTemplate("/hello/hello");

        // when
        String actual1 = sut.getRequestValueAnyway("hello");
        String actual2 = sut.getRequestValueAnyway("worlD");

        // then
        Asserts.assertEquals(actual1, "world");
        Asserts.assertEquals(actual2, null);

        verify(request, times(0)).getAttribute("hello");
        verify(request, times(1)).getAttribute("worlD");
        verify(request, times(1)).getAttribute("world");
        verify(request, times(1)).getAttribute("World");
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getHeader("hello");
        verify(request, times(1)).getHeader("worlD");
        verify(request, times(1)).getHeader("world");
        verify(request, times(1)).getHeader("World");
        verify(request, times(1)).getParameterValues("hello");
        verify(request, times(1)).getParameterValues("worlD");
        verify(request, times(1)).getParameterValues("world");
        verify(request, times(1)).getParameterValues("World");
        verifyNoMoreInteractions(request);
    }

    @Test
    public void getRequestValueAnywayCaseInParameterTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/hello/world");
        sut.setRequest(request);
        when(request.getParameterValues("hello")).thenReturn(new String[] {"world"});
        sut.setUriTemplate("/hello/hello");

        // when
        String actual1 = sut.getRequestValueAnyway("hello");
        String actual2 = sut.getRequestValueAnyway("worlD");

        // then
        Asserts.assertEquals(actual1, "world");
        Asserts.assertEquals(actual2, null);

        verify(request, times(0)).getAttribute("hello");
        verify(request, times(1)).getAttribute("worlD");
        verify(request, times(1)).getAttribute("world");
        verify(request, times(1)).getAttribute("World");
        verify(request, times(1)).getRequestURI();
        verify(request, times(0)).getHeader("hello");
        verify(request, times(1)).getHeader("worlD");
        verify(request, times(1)).getHeader("world");
        verify(request, times(1)).getHeader("World");
        verify(request, times(1)).getParameterValues("hello");
        verify(request, times(1)).getParameterValues("worlD");
        verify(request, times(1)).getParameterValues("world");
        verify(request, times(1)).getParameterValues("World");
        verifyNoMoreInteractions(request);
    }

    @Test
    public void getRequestValueAnywayCaseInCookieTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/hello/world");
        sut.setRequest(request);
        sut.setUriTemplate("/hello/hello");
        sut.setCookies(new Cookie[] {new Cookie("hello", "world")});

        // when
        String actual1 = sut.getRequestValueAnyway("hello");
        String actual2 = sut.getRequestValueAnyway("worlD");

        // then
        Asserts.assertEquals(actual1, "world");
        Asserts.assertEquals(actual2, null);

        verify(request, times(0)).getAttribute("hello");
        verify(request, times(1)).getAttribute("worlD");
        verify(request, times(1)).getAttribute("world");
        verify(request, times(1)).getAttribute("World");
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getHeader("hello");
        verify(request, times(1)).getHeader("worlD");
        verify(request, times(1)).getHeader("world");
        verify(request, times(1)).getHeader("World");
        verify(request, times(1)).getParameterValues("hello");
        verify(request, times(1)).getParameterValues("worlD");
        verify(request, times(1)).getParameterValues("world");
        verify(request, times(1)).getParameterValues("World");
        verifyNoMoreInteractions(request);
    }

    @Test
    public void getRequestValueAnywayCaseInArgumentTest() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/hello/world");
        sut.setRequest(request);
        sut.setUriTemplate("/hello/hello");
        sut.setArgument("hello", "world");

        // when
        String actual1 = sut.getRequestValueAnyway("hello");
        String actual2 = sut.getRequestValueAnyway("worlD");

        // then
        Asserts.assertEquals(actual1, "world");
        Asserts.assertEquals(actual2, null);

        verify(request, times(1)).getAttribute("hello");
        verify(request, times(1)).getAttribute("worlD");
        verify(request, times(1)).getAttribute("world");
        verify(request, times(1)).getAttribute("World");
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getHeader("hello");
        verify(request, times(1)).getHeader("worlD");
        verify(request, times(1)).getHeader("world");
        verify(request, times(1)).getHeader("World");
        verify(request, times(1)).getParameterValues("hello");
        verify(request, times(1)).getParameterValues("worlD");
        verify(request, times(1)).getParameterValues("world");
        verify(request, times(1)).getParameterValues("World");
        verifyNoMoreInteractions(request);
    }

    @Test
    public void getRequestValueAnywayCaseInArgument2Test() {
        // given
        SimpleRequestArguments sut = new SimpleRequestArguments();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/hello/world");
        sut.setRequest(request);
        sut.setUriTemplate("/hello/hello");
        sut.setArgument("hello", "world");

        // when
        String actual1 = sut.getRequestValueAnyway("hello");
        String actual2 = sut.getRequestValueAnyway("w");

        // then
        Asserts.assertEquals(actual1, "world");
        Asserts.assertEquals(actual2, null);

        verify(request, times(1)).getAttribute("hello");
        verify(request, times(2)).getAttribute("w");
        verify(request, times(1)).getAttribute("W");
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getHeader("hello");
        verify(request, times(2)).getHeader("w");
        verify(request, times(1)).getHeader("W");
        verify(request, times(1)).getParameterValues("hello");
        verify(request, times(2)).getParameterValues("w");
        verify(request, times(1)).getParameterValues("W");
        verifyNoMoreInteractions(request);
    }
}
