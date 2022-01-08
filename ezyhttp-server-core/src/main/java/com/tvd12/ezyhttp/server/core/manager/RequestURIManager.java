package com.tvd12.ezyhttp.server.core.manager;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tvd12.ezyfox.util.EzyDestroyable;

public class RequestURIManager implements EzyDestroyable {
    
    private final Set<String> handledURIs;
    private final Set<String> apiUris;
    private final Set<String> managementUris;
    private final Set<String> authenticatedUris;

    public RequestURIManager() {
        this.handledURIs = ConcurrentHashMap.newKeySet();
        this.apiUris = ConcurrentHashMap.newKeySet();
        this.managementUris = ConcurrentHashMap.newKeySet();
        this.authenticatedUris = ConcurrentHashMap.newKeySet();
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
    
    public Set<String> getHandledURIs() {
        return Collections.unmodifiableSet(handledURIs);
    }
    
    public Set<String> getApiUris() {
        return Collections.unmodifiableSet(apiUris);
    }
    
    public Set<String> getAuthenticatedUris() {
        return Collections.unmodifiableSet(authenticatedUris);
    }

    @Override
    public void destroy() {
        this.handledURIs.clear();
        this.managementUris.clear();
        this.authenticatedUris.clear();
    }
}
