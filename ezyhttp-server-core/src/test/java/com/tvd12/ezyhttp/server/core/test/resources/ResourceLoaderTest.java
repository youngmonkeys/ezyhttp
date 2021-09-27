package com.tvd12.ezyhttp.server.core.test.resources;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyhttp.server.core.resources.ResourceLoader;
import com.tvd12.test.assertion.Asserts;

public class ResourceLoaderTest {

	@Test
	public void urlNull() {
		// given
		ResourceLoader sut = new ResourceLoader();
		
		// when
		List<String> listResources = sut.listResources("not found");
		
		// then
		Asserts.assertTrue(listResources.isEmpty());
	}
	
	@Test
	public void urlPathEmpty() {
		// given
		ResourceLoader sut = new ResourceLoader() {
			@Override
			protected URL getResourceURL(String resource) {
				try {
					return new URL("http://locahost");
				} catch (MalformedURLException e) {
					e.printStackTrace();
					throw new IllegalArgumentException(e);
				}
			}
		};
		
		// when
		List<String> listResources = sut.listResources("application.yaml");
		
		// then
		Asserts.assertTrue(listResources.isEmpty());
	}
	
	@Test
	public void regexTest() {
		// given
		ResourceLoader sut = new ResourceLoader();
		
		// when
		List<String> listResources = sut.listResources("static", Sets.newHashSet("^static/css/.+"));
		
		// then
		Asserts.assertEquals(2, listResources.size());
	}
}
