package com.tvd12.ezyhttp.server.core.test.manager;

import java.util.Arrays;
import java.util.Collections;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.collections.Sets;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.manager.RequestURIManager;
import com.tvd12.test.assertion.Asserts;

public class RequestURIManagerTest {
    
    @Test
    public void test() {
        // given
        RequestURIManager sut = new RequestURIManager();
        sut.addHandledURI(HttpMethod.GET, "a");
        sut.addHandledURI(HttpMethod.GET, "a1");
        sut.addAuthenticatedURI(HttpMethod.POST, "b");
        sut.addAuthenticatedURI(HttpMethod.POST, "b1");
        sut.addApiURI(HttpMethod.PUT, "c");
        sut.addApiURI(HttpMethod.PUT, "c1");
        sut.addPaymentURI(HttpMethod.DELETE, "d");
        sut.addManagementURI(HttpMethod.DELETE, "e");
        
        // when
        // then
        Asserts.assertTrue(sut.containsHandledURI(HttpMethod.GET, "a"));
        Asserts.assertFalse(sut.containsHandledURI(HttpMethod.GET, "I don't know"));
        Asserts.assertFalse(sut.containsHandledURI(HttpMethod.PATCH, "I don't know"));
        Asserts.assertEquals(sut.getHandledURIs(HttpMethod.GET), Sets.newHashSet("a", "a1"), false);
        Asserts.assertEmpty(sut.getHandledURIs(HttpMethod.PATCH));
        
        Assert.assertTrue(sut.isAuthenticatedURI(HttpMethod.POST, "b"));
        Asserts.assertFalse(sut.isAuthenticatedURI(HttpMethod.POST, "I don't know"));
        Asserts.assertFalse(sut.isAuthenticatedURI(HttpMethod.PATCH, "I don't know"));
        Asserts.assertEquals(sut.getAuthenticatedURIs(HttpMethod.POST), Sets.newHashSet("b", "b1"), false);
        Asserts.assertEmpty(sut.getAuthenticatedURIs(HttpMethod.PATCH));
        
        Assert.assertTrue(sut.isApiURI(HttpMethod.PUT, "c"));
        Asserts.assertFalse(sut.isApiURI(HttpMethod.PUT, "I don't know"));
        Asserts.assertFalse(sut.isApiURI(HttpMethod.PATCH, "I don't know"));
        Asserts.assertEquals(sut.getApiURIs(HttpMethod.PUT), Arrays.asList("c", "c1"), false);
        Asserts.assertEmpty(sut.getApiURIs(HttpMethod.PATCH));
        
        Assert.assertTrue(sut.isPaymentURI(HttpMethod.DELETE, "d"));
        Asserts.assertFalse(sut.isPaymentURI(HttpMethod.DELETE, "I don't know"));
        Asserts.assertFalse(sut.isPaymentURI(HttpMethod.PATCH, "I don't know"));
        Asserts.assertEquals(sut.getPaymentURIs(HttpMethod.DELETE), Collections.singletonList("d"), false);
        Asserts.assertEmpty(sut.getPaymentURIs(HttpMethod.PATCH));
        
        Assert.assertTrue(sut.isManagementURI(HttpMethod.DELETE, "e"));
        Asserts.assertFalse(sut.isManagementURI(HttpMethod.DELETE, "I don't know"));
        Asserts.assertFalse(sut.isManagementURI(HttpMethod.PATCH, "I don't know"));
        Asserts.assertEquals(sut.getManagementURIs(HttpMethod.DELETE), Collections.singletonList("e"), false);
        Asserts.assertEmpty(sut.getManagementURIs(HttpMethod.PATCH));
    }
}
