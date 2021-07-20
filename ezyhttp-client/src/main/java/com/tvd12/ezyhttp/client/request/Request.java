package com.tvd12.ezyhttp.client.request;

import java.util.Map;

import com.tvd12.ezyhttp.core.constant.HttpMethod;

public interface Request<T> {

	String getURL();
	
	HttpMethod getMethod(); 
	
	int getReadTimeout();
	
	int getConnectTimeout();
	
	RequestEntity<T> getEntity();
	
	Map<Integer, Class<?>> getResponseTypes();
	
}
