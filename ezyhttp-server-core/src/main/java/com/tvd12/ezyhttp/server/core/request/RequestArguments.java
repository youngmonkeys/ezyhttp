package com.tvd12.ezyhttp.server.core.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyfox.util.EzyReleasable;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.data.BodyData;

public interface RequestArguments extends BodyData, EzyReleasable {

	HttpMethod getMethod();
	
	HttpServletRequest getRequest();
	
	HttpServletResponse getResponse();
	
	<T> T getArgument(Object key);
	
	void setArgument(Object key, Object value);
	
	String getParameter(int index);
	
	String getParameter(String name);
	
	String getHeader(int index);
	
	String getHeader(String name);
	
	String getPathVariable(int index);
	
	String getPathVariable(String name);
	
}
