package com.tvd12.ezyhttp.server.core.reflect;

import java.lang.reflect.Parameter;

import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;

import lombok.Getter;

@Getter
public class RequestHandlerMethod {
	
	protected final EzyMethod method;
	protected final String requestURI;
	
	public RequestHandlerMethod(String rootURI, EzyMethod method) {
		this.method = method;
		this.requestURI = getRequestURI(rootURI);
	}
	
	protected String getRequestURI(String rootURI) {
		return rootURI + getRequestURIFragment();
	}
	
	protected String getRequestURIFragment() {
		String uri = "";
		DoGet doGet = method.getAnnotation(DoGet.class);
		uri = doGet.value();
		return uri;
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
