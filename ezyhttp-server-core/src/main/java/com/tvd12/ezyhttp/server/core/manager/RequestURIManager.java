package com.tvd12.ezyhttp.server.core.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tvd12.ezyfox.util.EzyDestroyable;

public class RequestURIManager implements EzyDestroyable {

    private final Set<String> apiURIs;
    private final Set<String> authenticatedURIs;
    private final Set<String> handledURIs;
    private final Set<String> managementURIs;
    private final Set<String> paymentURIs;

    public RequestURIManager() {
        this.apiURIs = ConcurrentHashMap.newKeySet();
        this.authenticatedURIs = ConcurrentHashMap.newKeySet();
        this.handledURIs = ConcurrentHashMap.newKeySet();
        this.managementURIs = ConcurrentHashMap.newKeySet();
        this.paymentURIs = ConcurrentHashMap.newKeySet();
    }
    
    public void addHandledURI(String uri) {
        this.handledURIs.add(uri);
    }
    
    public boolean containsHandledURI(String uri) {
        return this.handledURIs.contains(uri);
    }

    public void addApiURI(String uri) {
        this.apiURIs.add(uri);
    }

    public boolean isApiURI(String uri) {
        return apiURIs.contains(uri);
    }
    
    public void addManagementURI(String uri) {
        this.managementURIs.add(uri);
    }

    public boolean isManagementURI(String uri) {
        return managementURIs.contains(uri);
    }
    
    public void addAuthenticatedURI(String uri) {
        this.authenticatedURIs.add(uri);
    }

    public boolean isAuthenticatedURI(String uri) {
        return authenticatedURIs.contains(uri);
    }
    
    public void addPaymentURI(String uri) {
        this.paymentURIs.add(uri);
    }

    public boolean isPaymentURI(String uri) {
        return paymentURIs.contains(uri);
    }
    
    public List<String> getHandledURIs() {
        return new ArrayList<>(handledURIs);
    }
    
    public List<String> getApiURIs() {
        return new ArrayList<>(apiURIs);
    }
    
    public List<String> getAuthenticatedURIs() {
        return new ArrayList<>(authenticatedURIs);
    }
    
    public List<String> getPaymentURIs() {
        return new ArrayList<>(paymentURIs);
    }

    @Override
    public void destroy() {
        this.handledURIs.clear();
        this.managementURIs.clear();
        this.authenticatedURIs.clear();
    }
}
