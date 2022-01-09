package com.tvd12.ezyhttp.server.core.test.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.testng.annotations.Test;

import com.beust.jcommander.internal.Lists;
import com.tvd12.ezyhttp.server.core.view.Redirect;
import com.tvd12.test.assertion.Asserts;

public class RedirectTest {

	@Test
	public void toTest() {
		// given
		Redirect sut = Redirect.to("/home");
		
		// when
		String uri = sut.getUri();
		
		// then
		Asserts.assertEquals("/home", uri);
	}
	
	@Test
	public void getQueryStringTest() throws Exception {
		// given
		Redirect sut = Redirect.builder()
				.uri("/home")
				.addHeader("foo", "bar")
				.addHeader("hello", "world")
				.addHeaders(Collections.singletonMap("one", new Integer(1)))
				.addParameter("param1", "one")
				.addParameter("param2", "two")
				.addParameters(Collections.singletonMap("param3", "three"))
				.addCookie("cookie1", "cvalue1")
				.addCookie("cookie2", "cvalue2")
				.addCookie("cookie3", "cvalue3", "/path")
				.build();
		
		// when
		String uri = sut.getUri();
		Map<String, String> headers = sut.getHeaders();
		List<Cookie> cookies = sut.getCookies();
		
		// then
		Asserts.assertEquals("/home", uri);
		Asserts.assertEquals("?param1=one&param2=two&param3=three", sut.getQueryString());
		
		Map<String, Object> expectedHeaders = new HashMap<>();
		expectedHeaders.put("foo", "bar");
		expectedHeaders.put("hello", "world");
		expectedHeaders.put("one", "1");
		Asserts.assertEquals(expectedHeaders, headers, false);
		
		Cookie cookie3 = new Cookie("cookie3", "cvalue3");
		cookie3.setPath("/path");
		List<Cookie> expectedCookies = Lists.newArrayList(
			new Cookie("cookie1", "cvalue1"),
			new Cookie("cookie2", "cvalue2"),
			cookie3
		);
		Asserts.assertEquals(expectedCookies, cookies, false);
		Asserts.assertEquals(sut.getCookies().get(2).getPath(), "/path");
	}
}
