package com.tvd12.ezyhttp.server.core.request;

import com.tvd12.ezyhttp.core.constant.HttpMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestURI {

	protected final String uri;
	protected final HttpMethod method;
	protected final boolean api;
	protected final boolean management;
	protected final boolean resource;
	protected final boolean authenticated;
	protected final String resourceFullPath;
	
	public RequestURI(
            HttpMethod method, 
            String uri, 
            boolean management) {
        this(method, uri, management, false, true);
    }
	
	public RequestURI(
            HttpMethod method, 
            String uri, 
            boolean management, 
            boolean authenticated,
            boolean api) {
	    this(method, uri, management, authenticated, false, api, null);
	}
	
	public RequestURI(
            HttpMethod method, 
            String uri, 
            boolean management,
            boolean resource,
            boolean api,
            String resourceFullPath) {
	    this(method, uri, management, false, resource, api, resourceFullPath);
	}
	
	private RequestURI(
	        HttpMethod method, 
	        String uri, 
	        boolean management,
	        boolean authenticated,
	        boolean resource,
	        boolean api,
	        String resourceFullPath) {
		this.method = method;
		this.uri = standardizeURI(uri);
		this.api = api;
		this.management = management;
		this.authenticated = authenticated;
		this.resource = resource;
		this.resourceFullPath = resourceFullPath;
	}
	
	protected String standardizeURI(String uri) {
		if(uri.isEmpty() || uri.startsWith("/"))
			return uri;
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
		if(other == null)
			return false;
		if(other == this)
			return true;
		if(!other.getClass().equals(this.getClass()))
			return false;
		RequestURI t = (RequestURI)other;
		if(uri.equals(t.uri) 
		        && method.equals(t.method) 
		        && management == t.management) {
			return true;
		}
		return false;
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
		return new StringBuilder()
				.append(uri).append(" - ").append(method)
				.toString();
	}
	
}
