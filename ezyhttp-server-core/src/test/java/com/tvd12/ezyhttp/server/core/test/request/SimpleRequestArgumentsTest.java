package com.tvd12.ezyhttp.server.core.test.request;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.request.SimpleRequestArguments;
import com.tvd12.test.assertion.Asserts;

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
	
	@SuppressWarnings("unchecked")
	@Test
	public void parameterTest() {
		// given
		SimpleRequestArguments sut = new SimpleRequestArguments();
		sut.setParameter("hello", "world");
		sut.setParameter("foo", "bar");
		
		// when
		String paramOverSize = sut.getParameter(3);
		String paramByName = sut.getParameter("hello");
		Map<String, String> params = sut.getParameters();
		
		// then
		Asserts.assertNull(paramOverSize);
		Asserts.assertEquals("world", paramByName);
		
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
		sut.setHeader("hello", "world");
		sut.setHeader("foo", "bar");
		
		// when
		String headerOverSize = sut.getHeader(3);
		String headerByName = sut.getHeader("hello");
		
		// then
		Asserts.assertNull(headerOverSize);
		Asserts.assertEquals("world", headerByName);
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
		sut.setCookies(new Cookie[] { new Cookie("hello", "world") });
		
		// when
		// then
		Asserts.assertNull(sut.getCookieValue(3));
		Asserts.assertEquals("world", sut.getCookieValue(0));
		Asserts.assertEquals("world", sut.getCookie("hello").getValue());
		Asserts.assertEquals("world", sut.getCookieValue("hello"));
		Asserts.assertNull(sut.getCookie("unknown"));
		sut.release();
	}
}
