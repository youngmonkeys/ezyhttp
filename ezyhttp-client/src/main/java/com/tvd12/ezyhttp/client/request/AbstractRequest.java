package com.tvd12.ezyhttp.client.request;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

import lombok.Getter;

@Getter
@SuppressWarnings({"unchecked"})
public abstract class AbstractRequest<R extends AbstractRequest<R>> implements Request {

	protected String url; 
	protected int readTimeout;
	protected int connectTimeout;
	protected RequestEntity entity;
	protected final Map<Integer, Class<?>> responseTypes;
	
	public AbstractRequest() {
		this.responseTypes = new HashMap<>();
	}
	
	public String getURL() {
		return this.url;
	}
	
	public R setURL(URI uri) {
		this.url = uri.toString();
		return (R)this;
	}
	
	public R setURL(URL url) {
		this.url = url.toString();
		return (R)this;
	}
	
	public R setURL(String url) {
		this.url = url;
		return (R)this;
	}
	
	public R setEntity(RequestEntity entity) {
		this.entity = entity;
		return (R)this;
	}
	
	public R setEntity(Object requestBody) {
		return setEntity(RequestEntity.body(requestBody));
	}
	
	public R setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
		return (R)this;
	}
	
	public R setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return (R)this;
	}
	
	public R setResponseType(Class<?> responseType) {
		return setResponseType(StatusCodes.OK, responseType);
	}
	
	public R setResponseType(int statusCode, Class<?> responseType) {
		this.responseTypes.put(statusCode, responseType);
		return (R)this;
	}
	
}
