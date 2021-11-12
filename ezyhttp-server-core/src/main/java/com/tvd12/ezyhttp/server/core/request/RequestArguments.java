package com.tvd12.ezyhttp.server.core.request;

import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyfox.util.EzyReleasable;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.data.BodyData;

public interface RequestArguments extends BodyData, EzyReleasable {

	HttpMethod getMethod();
	
	HttpServletRequest getRequest();
	
	HttpServletResponse getResponse();
	
	AsyncContext getAsynContext();
	
	boolean isAsyncStarted();
	
	<T> T getArgument(Object key);
	
	void setArgument(Object key, Object value);
	
	String getParameter(int index);
	
	String getParameter(String name);
	
	String getHeader(int index);
	
	String getHeader(String name);
	
	String getPathVariable(int index);
	
	String getPathVariable(String name);
	
	Cookie getCookie(String name);
	
	String getCookieValue(int index);
	
	String getCookieValue(String name);
	
	<T> T getRedirectionAttribute(String name);
	
	<T> T getRedirectionAttribute(String name, Class<T> outType);
	
	Map<String, Object> getRedirectionAttributes();
	
	default String getParameter(int index, String defaultValue) {
	    String answer = getParameter(index);
	    return answer != null ? answer : defaultValue;
	}
    
	default String getParameter(String name, String defaultValue) {
	    String answer = getParameter(name);
        return answer != null ? answer : defaultValue;
	}

    default String getHeader(String name, String defaultValue) {
        String answer = getHeader(name);
        return answer != null ? answer : defaultValue;
    }
    
    default String getHeader(int index, String defaultValue) {
        String answer = getHeader(index);
        return answer != null ? answer : defaultValue;
    }
    
    default String getCookieValue(int index, String defaultValue) {
        String answer = getCookieValue(index);
        return answer != null ? answer : defaultValue;
    }
    
    default String getCookieValue(String name, String defaultValue) {
        String answer = getCookieValue(name);
        return answer != null ? answer : defaultValue;
    }
}
