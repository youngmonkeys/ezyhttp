package com.tvd12.ezyhttp.server.core.test.resources;

import java.util.Collections;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.resources.ResourceLoader;

public class ResourceLoaderTest {

	@Test
	public void test() {
		ResourceLoader loader = new ResourceLoader();
		loader.listResources("templates", Collections.emptySet())
			  .forEach(System.out::println);
	}
	
}
