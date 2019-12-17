package com.tvd12.ezyhttp.client.request;

import lombok.Getter;

@Getter
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractRequest<R extends AbstractRequest<R>> implements Request {

	protected String url; 
	protected int readTimeout;
	protected int connectTimeout;
	protected Class responseType;
	protected RequestEntity entity;
	
	public String getURL() {
		return this.url;
	}
	
	public R setURL(String url) {
		this.url = url;
		return (R)this;
	}
	
	public R setEntity(RequestEntity entity) {
		this.entity = entity;
		return (R)this;
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
		this.responseType = responseType;
		return (R)this;
	}
	
}
