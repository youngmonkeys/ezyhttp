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
        sut.addAuthenticatedURI("b");
        sut.addApiURI("c");
        sut.addPaymentURI("d");
        sut.addManagementURI("e");
        
        // when
        // then
        Asserts.assertTrue(sut.containsHandledURI("a"));
        Asserts.assertEquals(sut.getHandledURIs(), Arrays.asList("a"), false);
        Assert.assertTrue(sut.isAuthenticatedURI("b"));
        Asserts.assertEquals(sut.getAuthenticatedURIs(), Arrays.asList("b"), false);
        Assert.assertTrue(sut.isApiURI("c"));
        Asserts.assertEquals(sut.getApiURIs(), Arrays.asList("c"), false);
        Assert.assertTrue(sut.isPaymentURI("d"));
        Asserts.assertEquals(sut.getPaymentURIs(), Arrays.asList("d"), false);
        Assert.assertTrue(sut.isManagementURI("e"));
        Asserts.assertEquals(sut.getManagementURIs(), Arrays.asList("e"), false);
    }
}
