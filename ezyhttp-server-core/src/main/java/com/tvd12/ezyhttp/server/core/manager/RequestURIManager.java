package com.tvd12.ezyhttp.server.core.manager;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tvd12.ezyfox.util.EzyDestroyable;

public class RequestURIManager implements EzyDestroyable {
    
    private final Set<String> handledURIs;
    private final Set<String> authenticatedUris;

    public RequestURIManager() {
        this.handledURIs = ConcurrentHashMap.newKeySet();
        this.authenticatedUris = ConcurrentHashMap.newKeySet();
    }
    
    public void addHandledURI(String uri) {
        this.handledURIs.add(uri);
    }
    
    public boolean containsHandledURI(String uri) {
        return this.handledURIs.contains(uri);
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
    
    public Set<String> getAuthenticatedUris() {
        return Collections.unmodifiableSet(authenticatedUris);
    }

    @Override
    public void destroy() {
        this.handledURIs.clear();
        this.authenticatedUris.clear();
    }
}
