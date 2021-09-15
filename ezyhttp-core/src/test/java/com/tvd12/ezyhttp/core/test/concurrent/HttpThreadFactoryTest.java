package com.tvd12.ezyhttp.core.test.concurrent;

import com.tvd12.ezyhttp.core.concurrent.HttpThreadFactory;
import com.tvd12.test.assertion.Asserts;

import org.testng.annotations.Test;

public class HttpThreadFactoryTest {

	@Test
	public void test() {
		// given
		HttpThreadFactory sut = HttpThreadFactory.create("test");
		
		// when
		Thread thread = sut.newThread(() ->  {});
		
		// then
		Asserts.assertTrue(thread.getName().contains("ezyhttp"));
	}
	
}
