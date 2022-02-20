package com.tvd12.ezyhttp.server.core.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tvd12.ezyfox.util.EzyDestroyable;

public class RequestURIManager implements EzyDestroyable {

    private final Set<String> apiUris;
    private final Set<String> authenticatedUris;
    private final Set<String> handledURIs;
    private final Set<String> managementUris;
    private final Set<String> paymentUris;

    public RequestURIManager() {
        this.apiUris = ConcurrentHashMap.newKeySet();
        this.authenticatedUris = ConcurrentHashMap.newKeySet();
        this.handledURIs = ConcurrentHashMap.newKeySet();
        this.managementUris = ConcurrentHashMap.newKeySet();
        this.paymentUris = ConcurrentHashMap.newKeySet();
    }
    
    public void addHandledURI(String uri) {
        this.handledURIs.add(uri);
    }
    
    public boolean containsHandledURI(String uri) {
        return this.handledURIs.contains(uri);
    }

    public void addApiUri(String uri) {
        this.apiUris.add(uri);
    }

    public boolean isApiUri(String uri) {
        return apiUris.contains(uri);
    }
    
    public void addManagementUri(String uri) {
        this.managementUris.add(uri);
    }

    public boolean isManagementUri(String uri) {
        return managementUris.contains(uri);
    }
    
    public void addAuthenticatedUri(String uri) {
        this.authenticatedUris.add(uri);
    }

    public boolean isAuthenticatedUri(String uri) {
        return authenticatedUris.contains(uri);
    }
    
    public void addPaymentUri(String uri) {
        this.paymentUris.add(uri);
    }

    public boolean isPaymentUri(String uri) {
        return paymentUris.contains(uri);
    }
    
    public List<String> getHandledURIs() {
        return new ArrayList<>(handledURIs);
    }
    
    public List<String> getApiUris() {
        return new ArrayList<>(apiUris);
    }
    
    public List<String> getAuthenticatedUris() {
        return new ArrayList<>(authenticatedUris);
    }
    
    public List<String> getPaymentUris() {
        return new ArrayList<>(paymentUris);
    }

    @Override
    public void destroy() {
        this.handledURIs.clear();
        this.managementUris.clear();
        this.authenticatedUris.clear();
    }
}
