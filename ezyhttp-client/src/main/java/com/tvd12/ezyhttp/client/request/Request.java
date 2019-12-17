package com.tvd12.ezyhttp.client.request;

import com.tvd12.ezyhttp.core.constant.HttpMethod;

public interface Request {

	String getURL();
	
	HttpMethod getMethod(); 
	
	int getReadTimeout();
	
	int getConnectTimeout();
	
	RequestEntity getEntity();
	
	<T> Class<T> getResponseType();
	
}
