package com.tvd12.ezyhttp.core.test.constant;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.test.assertion.Asserts;

public class HttpMethodTest {

	@Test
	public void test() {
		Asserts.assertEquals(1, HttpMethod.GET.getId());
		Asserts.assertEquals("get", HttpMethod.GET.getName());
		
		Asserts.assertFalse(HttpMethod.GET.hasOutput());
		Asserts.assertFalse(HttpMethod.DELETE.hasOutput());
		Asserts.assertTrue(HttpMethod.POST.hasOutput());
		Asserts.assertTrue(HttpMethod.PUT.hasOutput());
		Asserts.assertTrue(HttpMethod.PATCH.hasOutput());
	}
}
