package com.tvd12.ezyhttp.core.test.net;

import java.net.URI;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.net.URIBuilder;

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
	
}
