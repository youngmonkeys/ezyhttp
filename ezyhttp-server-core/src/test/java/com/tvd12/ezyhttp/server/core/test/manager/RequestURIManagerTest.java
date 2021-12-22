package com.tvd12.ezyhttp.server.core.test.manager;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.manager.RequestURIManager;
import com.tvd12.test.assertion.Asserts;

public class RequestURIManagerTest {
    
    @Test
    public void test() {
        // given
        RequestURIManager sut = new RequestURIManager();
        sut.addHandledURI("a");
        sut.addAuthenticatedUri("b");
        sut.addApiUri("c");
        
        // when
        // then
        Asserts.assertTrue(sut.containsHandledURI("a"));
        Asserts.assertEquals(sut.getHandledURIs(), Arrays.asList("a"), false);
        Assert.assertTrue(sut.isAuthenticatedUri("b"));
        Asserts.assertEquals(sut.getAuthenticatedUris(), Arrays.asList("b"), false);
        Assert.assertTrue(sut.isApiUri("c"));
        Asserts.assertEquals(sut.getApiUris(), Arrays.asList("c"), false);
    }
}
