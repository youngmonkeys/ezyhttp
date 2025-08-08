package com.tvd12.ezyhttp.server.core.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyhttp.core.constant.HttpMethod;

public class RequestURIManager implements EzyDestroyable {

    private final Map<HttpMethod, Set<String>> apiURIs;
    private final Map<HttpMethod, Set<String>> authenticatedURIs;
    private final Map<HttpMethod, Set<String>> authenticatableURIs;
    private final Map<HttpMethod, Set<String>> handledURIs;
    private final Map<HttpMethod, Set<String>> managementURIs;
    private final Map<HttpMethod, Set<String>> paymentURIs;

    public RequestURIManager() {
        this.apiURIs = new ConcurrentHashMap<>();
        this.authenticatedURIs = new ConcurrentHashMap<>();
        this.authenticatableURIs = new ConcurrentHashMap<>();
        this.handledURIs = new ConcurrentHashMap<>();
        this.managementURIs = new ConcurrentHashMap<>();
        this.paymentURIs = new ConcurrentHashMap<>();
    }

    public void addHandledURI(HttpMethod method, String uri) {
        this.handledURIs
            .computeIfAbsent(method, k -> ConcurrentHashMap.newKeySet())
            .add(uri);
    }

    public boolean containsHandledURI(HttpMethod method, String uri) {
        Set<String> uris = handledURIs.get(method);
        return uris != null && uris.contains(uri);
    }

    public void addApiURI(HttpMethod method, String uri) {
        this.apiURIs
            .computeIfAbsent(method, k -> ConcurrentHashMap.newKeySet())
            .add(uri);
    }

    public boolean isApiURI(HttpMethod method, String uri) {
        Set<String> uris = apiURIs.get(method);
        return uris != null && uris.contains(uri);
    }

    public void addManagementURI(HttpMethod method, String uri) {
        this.managementURIs
            .computeIfAbsent(method, k -> ConcurrentHashMap.newKeySet())
            .add(uri);
    }

    public boolean isManagementURI(HttpMethod method, String uri) {
        Set<String> uris = managementURIs.get(method);
        return uris != null && uris.contains(uri);
    }

    public void addAuthenticatedURI(HttpMethod method, String uri) {
        this.authenticatedURIs
            .computeIfAbsent(method, k -> ConcurrentHashMap.newKeySet())
            .add(uri);
    }

    public boolean isAuthenticatedURI(HttpMethod method, String uri) {
        Set<String> uris = authenticatedURIs.get(method);
        return uris != null && uris.contains(uri);
    }

    public void addAuthenticatableURI(HttpMethod method, String uri) {
        this.authenticatableURIs
            .computeIfAbsent(method, k -> ConcurrentHashMap.newKeySet())
            .add(uri);
    }

    public boolean isAuthenticatableURI(HttpMethod method, String uri) {
        Set<String> uris = authenticatableURIs.get(method);
        return uris != null && uris.contains(uri);
    }

    public void addPaymentURI(HttpMethod method, String uri) {
        this.paymentURIs
            .computeIfAbsent(method, k -> ConcurrentHashMap.newKeySet())
            .add(uri);
    }

    public boolean isPaymentURI(HttpMethod method, String uri) {
        Set<String> uris = paymentURIs.get(method);
        return uris != null && uris.contains(uri);
    }

    public List<String> getHandledURIs(HttpMethod method) {
        Set<String> uris = handledURIs.get(method);
        return uris != null ? new ArrayList<>(uris) : Collections.emptyList();
    }

    public List<String> getApiURIs(HttpMethod method) {
        Set<String> uris = apiURIs.get(method);
        return uris != null ? new ArrayList<>(uris) : Collections.emptyList();
    }

    public List<String> getAuthenticatedURIs(HttpMethod method) {
        Set<String> uris = authenticatedURIs.get(method);
        return uris != null ? new ArrayList<>(uris) : Collections.emptyList();
    }

    public List<String> getManagementURIs(HttpMethod method) {
        Set<String> uris = managementURIs.get(method);
        return uris != null ? new ArrayList<>(uris) : Collections.emptyList();
    }

    public List<String> getPaymentURIs(HttpMethod method) {
        Set<String> uris = paymentURIs.get(method);
        return uris != null ? new ArrayList<>(uris) : Collections.emptyList();
    }

    @Override
    public void destroy() {
        this.handledURIs.clear();
        this.managementURIs.clear();
        this.authenticatedURIs.clear();
    }
}
