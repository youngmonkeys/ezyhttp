package com.tvd12.ezyhttp.server.core.test.request;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.request.RequestURI;
import com.tvd12.test.assertion.Asserts;

public class RequestURITest {

	@Test
	public void test() {
		// given
		RequestURI uri1 = new RequestURI(HttpMethod.GET, "/");
		RequestURI uri2 = new RequestURI(HttpMethod.GET, "/");
		RequestURI uri3 = new RequestURI(HttpMethod.POST, "/");
		RequestURI uri4 = new RequestURI(HttpMethod.GET, "/api");
		RequestURI uri5 = new RequestURI(HttpMethod.PUT, "/api/v1");
		RequestURI uri6 = new RequestURI(HttpMethod.GET, "");
		
		// when
		// then
		Asserts.assertEquals(uri1.getMethod(), HttpMethod.GET);
		assert !uri1.equals(null);
		assert uri1.equals(uri1);
		assert !uri1.equals(new Object());
		Asserts.assertEquals(uri1, uri1);
		Asserts.assertEquals(uri1, uri2);
		Asserts.assertNotEquals(uri1, uri3);
		Asserts.assertNotEquals(uri1, uri4);
		Asserts.assertNotEquals(uri1, uri5);
		Asserts.assertNotEquals(uri1, uri6);
		
	}
}