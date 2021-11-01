package com.tvd12.ezyhttp.server.core.reflect;

import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.net.URIBuilder;
import com.tvd12.ezyhttp.server.core.annotation.Authenticated;
import com.tvd12.ezyhttp.server.core.annotation.DoDelete;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.DoPost;
import com.tvd12.ezyhttp.server.core.annotation.DoPut;
import com.tvd12.ezyhttp.server.core.util.DoDeleteAnnotations;
import com.tvd12.ezyhttp.server.core.util.DoGetAnnotations;
import com.tvd12.ezyhttp.server.core.util.DoPostAnnotations;
import com.tvd12.ezyhttp.server.core.util.DoPutAnnotations;

import lombok.Getter;

@Getter
public class RequestHandlerMethod extends HandlerMethod {
	
	protected final String requestURI;
	protected final String responseType;
	protected final HttpMethod httpMethod;
	
	public RequestHandlerMethod(String rootURI, EzyMethod method) {
		super(method);
		this.requestURI = fetchRequestURI(rootURI);
		this.httpMethod = fetchHttpMethod();
		this.responseType = fetchResponseType();
	}
	
	protected String fetchRequestURI(String rootURI) {
		String uri = rootURI + fetchRequestURIFragment();
		return URIBuilder.normalizePath(uri);
	}
	
	protected String fetchRequestURIFragment() {
		DoGet doGet = method.getAnnotation(DoGet.class);
		if(doGet != null)
			return DoGetAnnotations.getURI(doGet);
		DoPost doPost = method.getAnnotation(DoPost.class);
		if(doPost != null)
			return DoPostAnnotations.getURI(doPost);
		DoPut doPut = method.getAnnotation(DoPut.class);
		if(doPut != null)
			return DoPutAnnotations.getURI(doPut);
		DoDelete doDelete = method.getAnnotation(DoDelete.class);
		return DoDeleteAnnotations.getURI(doDelete);
	}
	
	protected HttpMethod fetchHttpMethod() {
		DoGet doGet = method.getAnnotation(DoGet.class);
		if(doGet != null)
			return HttpMethod.GET;
		DoPost doPost = method.getAnnotation(DoPost.class);
		if(doPost != null)
			return HttpMethod.POST;
		DoPut doPut = method.getAnnotation(DoPut.class);
		if(doPut != null)
			return HttpMethod.PUT;
		return HttpMethod.DELETE;
	}
	
	protected String fetchResponseType() {
		DoGet doGet = method.getAnnotation(DoGet.class);
		if(doGet != null)
			return DoGetAnnotations.getResponseType(doGet);
		DoPost doPost = method.getAnnotation(DoPost.class);
		if(doPost != null)
			return DoPostAnnotations.getResponseType(doPost);
		DoPut doPut = method.getAnnotation(DoPut.class);
		if(doPut != null)
			return DoPutAnnotations.getResponseType(doPut);
		DoDelete doDelete = method.getAnnotation(DoDelete.class);
		return DoDeleteAnnotations.getResponseType(doDelete);
	}
	
	public boolean isAuthenticated() {
	    return method.isAnnotated(Authenticated.class);
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(method.getName())
				.append("(")
					.append("uri: ").append(requestURI)
				.append(")")
				.toString();
	}
}
