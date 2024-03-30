package com.tvd12.ezyhttp.core.test.net;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.net.URITree;
import com.tvd12.test.assertion.Asserts;

public class URITreeTest {

    @Test
    public void test() {
        // given
        String uri11 = "/api/v1/customer/{name}/create";
        String uri12 = "/api/v1/customer/{name}/delete";
        String uri2 = "/api/v1/user";

        // when
        URITree tree = new URITree();
        tree.addURI(uri11);
        tree.addURI(uri12);
        tree.addURI(uri2);

        // then
        System.out.println("tree: " + tree);
        String check11 = "/api/v1/customer/dung/create";
        String check12 = "/api/v1/customer/dung/create/1";

        Assert.assertEquals("/api/v1/customer/{name}/create", tree.getMatchedURI(check11));
        System.out.println("check11: " + tree.getMatchedURI(check11));

        Assert.assertNull(tree.getMatchedURI(check12));
        System.out.println("check12: " + tree.getMatchedURI(check12));
    }

    @Test
    public void getMatchedURIChildNull() {
        // given
        URITree tree = new URITree();
        tree.addURI("/api/v1/hello/{world}/{foo}/{bar}");

        // when
        // then
        System.out.println(tree);
        Asserts.assertNull(tree.getMatchedURI("/api/v1"));
        Asserts.assertNull(tree.getMatchedURI("/api/v1/hello"));
        Asserts.assertNull(tree.getMatchedURI("/api/v1/hello/{world}"));
        Asserts.assertNull(tree.getMatchedURI("/api/v1/hello/{world}/{foo}"));
        Asserts.assertNull(tree.getMatchedURI("/api/v1/hello/{world}/{foo}/{bar}/unknown"));
    }

    @Test
    public void getMatchedURIChildNull2() {
        // given
        URITree tree = new URITree();
        tree.addURI("/api/v1/hello/{world}/{foo}/bar");

        // when
        // then
        System.out.println(tree);
        Asserts.assertNull(tree.getMatchedURI("/api/v1"));
        Asserts.assertNull(tree.getMatchedURI("/api/v1/hello"));
        Asserts.assertNull(tree.getMatchedURI("/api/v1/hello/{world}"));
        Asserts.assertNull(tree.getMatchedURI("/api/v1/hello/{world}/{foo}"));
        Asserts.assertNull(tree.getMatchedURI("/api/v1/hello/{world}/{foo}/{bar}/unknown"));
    }

    @Test
    public void matchAllTest() {
        // given
        URITree tree = new URITree();
        tree.addURI("/market/items/hello/java/docs/*");

        // when
        // then
        System.out.println(tree);
        Asserts.assertNull(tree.getMatchedURI("/market/items/hello/java"));
        Asserts.assertNull(tree.getMatchedURI("/market/items/hello/java/docs"));
        Asserts.assertNull(tree.getMatchedURI("/market/items/hello/java/docs/"));
        Asserts.assertNotNull(tree.getMatchedURI("/market/items/hello/java/docs/index.html"));
        Asserts.assertNotNull(tree.getMatchedURI("/market/items/hello/java/docs/com/tvd12/index.html"));
    }

    @Test
    public void matchUpperCaseTest() {
        // given
        URITree tree = new URITree();
        tree.addURI("/api/v1/oa/{code}/webhook");

        // when
        // then
        System.out.println(tree);
        Asserts.assertNotNull(tree.getMatchedURI("/api/v1/oa/ZALO/webhook"));
    }
}
