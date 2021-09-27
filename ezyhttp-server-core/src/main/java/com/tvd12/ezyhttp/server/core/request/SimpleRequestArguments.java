package com.tvd12.ezyhttp.server.core.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.net.PathVariables;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unchecked")
public class SimpleRequestArguments implements RequestArguments {

	@Setter
	@Getter
	protected HttpMethod method;
	@Setter
	protected String uriTemplate;
	@Getter
	protected HttpServletRequest request;
	@Getter
	protected HttpServletResponse response;
	protected Map<Object, Object> arguments;
	protected List<String> headerList;
	protected Map<String, String> headerMap;
	protected List<String> parameterList;
	protected Map<String, String> parameterMap;
	protected Map<String, String> pathVariableMap;
	protected List<Entry<String, String>> pathVariableList;
	protected Cookie[] cookies;
	protected Map<String, Cookie> cookieMap;
	
	@Override
	public <T> T getArgument(Object key) {
		if(arguments == null)
			return null;
		Object argument = arguments.get(key);
		return (T)argument;
	}
	
	@Override
	public void setArgument(Object key, Object value) {
		if(arguments == null)
			arguments = new HashMap<>();
		arguments.put(key, value);
	}
	
	@Override
	public String getParameter(int index) {
		if(parameterList == null)
			return null;
		if(parameterList.size() <= index)
			return null;
		String parameter = parameterList.get(index);
		return parameter;
	}
	
	@Override
	public String getParameter(String name) {
		if(parameterMap == null)
			return null;
		String parameter = parameterMap.get(name);
		return parameter;
	}
	
	@Override
	public Map<String, String> getParameters() {
		return parameterMap;
	}
	
	public void setParameter(String name, String value) {
		if(parameterList == null)
			parameterList = new ArrayList<>();
		if(parameterMap == null)
			parameterMap = new HashMap<>();
		parameterList.add(value);
		parameterMap.put(name, value);
	}
	
	@Override
	public String getHeader(int index) {
		if(headerList == null)
			return null;
		if(headerList.size() <= index)
			return null;
		String header = headerList.get(index);
		return header;
	}
	
	@Override
	public String getHeader(String name) {
		if(headerMap == null)
			return null;
		String header = headerMap.get(name);
		return header;
	}
	
	public void setHeader(String name, String value) {
		if(headerList == null)
			headerList = new ArrayList<>();
		if(headerMap == null)
			headerMap = new HashMap<>();
		headerList.add(value);
		headerMap.put(name, value);
	}
	
	@Override
	public String getPathVariable(int index) {
		fetchPathVariables();
		if(pathVariableList.size() <= index)
			return null;
		String varValue = pathVariableList.get(index).getValue();
		return varValue;
	}
	
	@Override
	public String getPathVariable(String name) {
		fetchPathVariables();
		String varValue = pathVariableMap.get(name);
		return varValue;
	}
	
	protected void fetchPathVariables() {
		if(pathVariableList == null) {
			pathVariableList = PathVariables.getVariables(uriTemplate, request.getRequestURI());
			pathVariableMap = new HashMap<>();
			for(Entry<String, String> entry : pathVariableList)
				pathVariableMap.put(entry.getKey(), entry.getValue());
		}
	}
	
	public void setRequest(HttpServletRequest request) {
		this.request = request;
		this.setArgument(HttpServletRequest.class, request);
	}
	
	public void setResponse(HttpServletResponse response) {
		this.response = response;
		this.setArgument(HttpServletResponse.class, response);
	}
	
	@Override
	public String getContentType() {
		return ContentTypes.getContentType(request.getContentType());
	}
	
	@Override
	public String getRequestContentType() {
		String type = request.getContentType();
		return type;
	}
	
	@Override
	public int getContentLength() {
		int length = request.getContentLength();
		return length;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return request.getInputStream();
	}
	
	public void setCookies(Cookie[] cookies) {
		if(cookies == null || cookies.length == 0)
			return;
		this.cookies = cookies;
		this.cookieMap = new HashMap<>();
		for(Cookie cookie : cookies)
			cookieMap.put(cookie.getName(), cookie);
	}
	
	@Override
	public String getCookieValue(int index) {
		if(cookies != null && cookies.length > index)
			return cookies[index].getValue();
		return null;
	}
	
	@Override
	public Cookie getCookie(String name) {
		return cookieMap != null ? cookieMap.get(name) : null;
	}
	
	@Override
	public String getCookieValue(String name) {
		Cookie cookie = getCookie(name);
		return cookie != null ? cookie.getValue() : null;
	}
	
	@Override
	public void release() {
		if(arguments != null)
			arguments.clear();
		if(headerList != null)
			headerList.clear();
		if(headerMap != null)
			headerMap.clear();
		if(parameterList != null)
			parameterList.clear();
		if(parameterMap != null)
			parameterMap.clear();
		this.arguments = null;
		this.headerList = null;
		this.headerMap = null;
		this.parameterList = null;
		this.parameterMap = null;
		this.cookies = null;
	}
}
