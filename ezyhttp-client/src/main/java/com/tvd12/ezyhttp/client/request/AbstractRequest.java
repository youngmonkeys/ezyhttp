package com.tvd12.ezyhttp.client.request;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyhttp.core.constant.StatusCodes;

import lombok.Getter;

/**
 * @author github.com/tvd12
 * @modifier: github.com/phamtiennam23
 * @param <R> Type of the response's body
 * @param <T> Type of the request's body
 */
@Getter
public abstract class AbstractRequest<R, T> implements Request<T> {

	protected String url; 
	protected int readTimeout;
	protected int connectTimeout;
	protected RequestEntity<T> entity;
	protected final Map<Integer, Class<?>> responseTypes;
	
	public AbstractRequest() {
		this.responseTypes = new HashMap<>();
	}
	
	public String getURL() {
		return this.url;
	}
	
	public AbstractRequest<R, T> setURL(URI uri) {
		this.url = uri.toString();
		return this;
	}
	
	public AbstractRequest<R, T> setURL(URL url) {
		this.url = url.toString();
		return this;
	}
	
	public AbstractRequest<R, T> setURL(String url) {
		this.url = url;
		return this;
	}
	
	public AbstractRequest<R, T> setEntity(RequestEntity<T> entity) {
		this.entity = entity;
		return this;
	}
	
	public AbstractRequest<R, T> setEntity(T requestBody) {
		return setEntity(new RequestEntity<>(requestBody));
	}
	
	public AbstractRequest<R, T> setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}
	
	public AbstractRequest<R, T> setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}
	
	public AbstractRequest<R, T> setResponseType(Class<?> responseType) {
		return setResponseType(StatusCodes.OK, responseType);
	}
	
	public AbstractRequest<R, T> setResponseType(int statusCode, Class<?> responseType) {
		this.responseTypes.put(statusCode, responseType);
		return this;
	}
	
}
