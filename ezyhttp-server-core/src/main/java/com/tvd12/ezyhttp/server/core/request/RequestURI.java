package com.tvd12.ezyhttp.server.core.request;

import com.tvd12.ezyhttp.core.constant.HttpMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("AbbreviationAsWordInName")
public class RequestURI {

    protected final String uri;
    protected final HttpMethod method;
    protected final boolean api;
    protected final boolean authenticated;
    protected final boolean management;
    protected final boolean resource;
    protected final boolean payment;
    protected final String feature;
    protected final String resourceFullPath;

    public RequestURI(
            HttpMethod method,
            String uri,
            boolean management
    ) {
        this.method = method;
        this.uri = standardizeURI(uri);
        this.management = management;
        this.api = false;
        this.authenticated = false;
        this.resource = false;
        this.payment = false;
        this.feature = null;
        this.resourceFullPath = null;
    }

    public RequestURI(
            HttpMethod method,
            String uri,
            boolean management,
            boolean resource,
            boolean api,
            String resourceFullPath
    ) {
        this(
                method,
                uri,
                RequestURIMeta.builder()
                        .api(api)
                        .authenticated(false)
                        .management(management)
                        .resource(resource)
                        .resourceFullPath(resourceFullPath)
                        .build()
        );
    }

    public RequestURI(
            HttpMethod method,
            String uri,
            RequestURIMeta meta
    ) {
        this.method = method;
        this.uri = standardizeURI(uri);
        this.api = meta.isApi();
        this.authenticated = meta.isAuthenticated();
        this.management = meta.isManagement();
        this.resource = meta.isResource();
        this.payment = meta.isPayment();
        this.feature = meta.getFeature();
        this.resourceFullPath = meta.getResourceFullPath();
    }

    protected String standardizeURI(String uri) {
        if (uri.isEmpty() || uri.startsWith("/")) {
            return uri;
        }
        return "/" + uri;
    }

    public String getSameURI() {
        return getSameURI(uri);
    }

    public static String getSameURI(String originalURI) {
        if (originalURI.length() <= 1) {
            return originalURI;
        }
        if (originalURI.endsWith("/")) {
            return originalURI.substring(0, originalURI.length() - 1);
        }
        return originalURI + "/";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!other.getClass().equals(this.getClass())) {
            return false;
        }
        RequestURI t = (RequestURI) other;
        return uri.equals(t.uri)
                && method.equals(t.method)
                && management == t.management;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = result * prime + uri.hashCode();
        result = result * prime + method.hashCode();
        result = result * prime + Boolean.hashCode(management);
        return result;
    }

    @Override
    public String toString() {
        return uri + " - " + method;
    }

}
