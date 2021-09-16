package com.tvd12.ezyhttp.core.test.net;

import java.net.URI;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.net.URIBuilder;
import com.tvd12.test.assertion.Asserts;

public class URIBuilderTest {

	@Test
	public void test() {
		URI uri = new URIBuilder("/abc")
				.addPath("/def/")
				.addPath("/xyz/")
				.addQueryParam("hell", "world")
				.addQueryParam("zig", "zag")
				.build();
		System.out.println(uri);
		uri = new URIBuilder()
				.addPath("/")
				.addPath("/xyz/")
				.addQueryParam("hell", "world")
				.addQueryParam("zig", "zag")
				.build();
		System.out.println(uri);
		uri = new URIBuilder()
				.addPath("/")
				.addPath("//")
				.addQueryParam("hell", "world")
				.addQueryParam("zig", "zag")
				.build();
		System.out.println(uri);
		uri = new URIBuilder()
				.addPath("/xzy/")
				.addQueryParam("hell", "world")
				.addQueryParam("zig", "zag")
				.build();
		System.out.println(uri);
	}
	
	@Test
	public void addEmptyPath() {
		// given
		// when
		URI uri = new URIBuilder()
				.addPath("")
				.build();
		
		// then
		Asserts.assertEquals(URI.create(""), uri);
	}
	
	@Test
	public void buildPathEmpty() {
		// given
		// when
		URI uri = new URIBuilder()
				.build();
		
		// then
		Asserts.assertEquals(URI.create(""), uri);
	}
	
	@Test
	public void normalizePathEmpty() {
		// given
		// when
		String path = URIBuilder.normalizePath("");
		
		// then
		Asserts.assertEquals("", path);
	}
}
