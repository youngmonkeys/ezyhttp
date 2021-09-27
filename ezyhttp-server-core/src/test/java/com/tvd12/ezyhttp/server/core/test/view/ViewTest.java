package com.tvd12.ezyhttp.server.core.test.view;

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
				.addVariable("variable1", "variable1Value")
				.addVariable("variable2", "variable2Value")
				.build();
		
		// when
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
					.build(), 
				sut.getVariables());
	}
	
	@Test
	public void ofTest() {
		// given
		View sut = View.of("home.html");
		
		// when
		// then
		Asserts.assertEquals("home.html", sut.getTemplate());
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
