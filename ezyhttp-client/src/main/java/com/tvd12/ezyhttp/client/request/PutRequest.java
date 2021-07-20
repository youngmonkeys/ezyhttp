package com.tvd12.ezyhttp.client.request;

import com.tvd12.ezyhttp.core.constant.HttpMethod;

public class PutRequest<T> extends AbstractRequest<PutRequest<T>, T> {

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.PUT;
	}
	
}
