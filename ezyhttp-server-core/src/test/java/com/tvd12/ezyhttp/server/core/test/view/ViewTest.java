package com.tvd12.ezyhttp.server.core.test.view;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.test.assertion.Asserts;

public class ViewTest {

	@Test
	public void test() {
		// given
		View sut = View.builder()
				.template("index.html")
				.locale(Locale.ENGLISH)
				.locale("vi")
				.contentType(ContentTypes.TEXT_HTML_UTF8)
				.addHeader("foo", "bar")
				.addHeader("hello", "world")
				.addHeaders(Collections.singletonMap("header", "headerValue"))
				.addCookie(new Cookie("cookie1", "cookie1Value"))
				.addCookie(new Cookie("cookie2", "cookie2Value"))
				.addCookie("cookie2", "cookie3Value", "/path")
				.addVariable("variable1", "variable1Value")
				.addVariable("variable2", "variable2Value")
				.appendToVariable("list", "a")
				.appendToVariable("list", "b")
				.build();
		
		// when
		sut.setVariable("setValue", "value");
		sut.setVariables(Collections.singletonMap("mapKey", "mapValue"));
        sut.appendToVariable("list", "c");
		
		// then
		Asserts.assertEquals("index.html", sut.getTemplate());
		Asserts.assertEquals(new Locale("vi"), sut.getLocale());
		Asserts.assertEquals(ContentTypes.TEXT_HTML_UTF8, sut.getContentType());
		Asserts.assertEquals(
				EzyMapBuilder.mapBuilder()
					.put("foo", "bar")
					.put("hello", "world")
					.put("header", "headerValue")
					.build(), 
				sut.getHeaders());
		Asserts.assertEquals("variable1Value", sut.getVariable("variable1"));
		Asserts.assertEquals("variable2Value", sut.getVariable("variable2"));
		Asserts.assertEquals(
				EzyMapBuilder.mapBuilder()
					.put("variable1", "variable1Value")
					.put("variable2", "variable2Value")
					.put("setValue", "value")
					.put("list", Arrays.asList("a", "b", "c"))
					.put("mapKey", "mapValue")
					.build(), 
				sut.getVariables());
		Asserts.assertEquals(sut.getVariable("setValue"), "value");
		Asserts.assertEquals(sut.getVariable("list"), Arrays.asList("a", "b", "c"), false);
		Asserts.assertTrue(sut.containsVariable("variable1"));
		Asserts.assertFalse(sut.containsVariable("i don't know"));
		Asserts.assertEquals(sut.getCookies().get(2).getPath(), "/path");
		Asserts.assertEquals(sut.getVariable("mapKey"), "mapValue");
	}
	
	@Test
	public void ofTest() {
		// given
		View sut = View.of("home.html");
		sut.setLocale(Locale.CANADA);
		
		// when
		// then
		Asserts.assertEquals("home.html", sut.getTemplate());
		Asserts.assertEquals(sut.getLocale(), Locale.CANADA);
	}

	@Test
	public void createFailedDueToTemplateIsNull() {
		// given
		// when
		Throwable e = Asserts.assertThrows(() -> View.builder().build());
		
		// then
		Asserts.assertThat(e).isEqualsType(NullPointerException.class);
	}
}
