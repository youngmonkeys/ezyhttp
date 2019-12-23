package com.tvd12.ezyhttp.core.test.net;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.net.URITree;

public class URITreeTest {

	@Test
	public void test() {
		String uri11 = "/api/v1/customer/{name}/create";
		String uri12 = "/api/v1/customer/{name}/delete";
		String uri2 = "/api/v1/user";
		URITree tree = new URITree();
		tree.addURI(uri11);
		tree.addURI(uri12);
		tree.addURI(uri2);
		System.out.println("tree: " + tree);
		String check11 = "/api/v1/customer/dung/create";
		String check12 = "/api/v1/customer/dung/create/1";
		System.out.println("check11: " + tree.getMatchedURI(check11));
		System.out.println("check12: " + tree.getMatchedURI(check12));
	}
	
	@Test
	public void test2() {
		URITree tree = new URITree();
		tree.addURI("/api/v1/customer/{name}/create");
		assert tree.containsURI("/api/v1/customer/abc/create");
	}

}
