package com.tvd12.ezyhttp.server.core.reflect;

import java.lang.reflect.Parameter;

import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.DoPost;
import com.tvd12.ezyhttp.server.core.annotation.DoPut;
import com.tvd12.ezyhttp.server.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.util.DoGetAnnotations;
import com.tvd12.ezyhttp.server.core.util.DoPostAnnotations;

import lombok.Getter;

@Getter
public class RequestHandlerMethod {
	
	protected final EzyMethod method;
	protected final String requestURI;
	protected final String responseType;
	protected final HttpMethod httpMethod;
	
	public RequestHandlerMethod(String rootURI, EzyMethod method) {
		this.method = method;
		this.requestURI = fetchRequestURI(rootURI);
		this.httpMethod = fetchHttpMethod();
		this.responseType = fetchResponseType();
	}
	
	protected String fetchRequestURI(String rootURI) {
		return rootURI + fetchRequestURIFragment();
	}
	
	protected String fetchRequestURIFragment() {
		String uri = "";
		DoGet doGet = method.getAnnotation(DoGet.class);
		if(doGet != null)
			uri = DoGetAnnotations.getURI(doGet);
		DoPost doPost = method.getAnnotation(DoPost.class);
		if(doPost != null)
			uri = DoPostAnnotations.getURI(doPost);
		return uri;
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
		String responseType = "";
		DoGet doGet = method.getAnnotation(DoGet.class);
		if(doGet != null)
			responseType = DoGetAnnotations.getResponseType(doGet);
		DoPost doPost = method.getAnnotation(DoPost.class);
		if(doPost != null)
			responseType = DoPostAnnotations.getResponseType(doPost);
		return responseType;
	}
	
	public String getName() {
		return method.getName();
	}
	
	public Parameter[] getParameters() {
		return method.getMethod().getParameters();
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
