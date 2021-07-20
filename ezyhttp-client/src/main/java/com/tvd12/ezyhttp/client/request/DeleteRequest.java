package com.tvd12.ezyhttp.client.request;

import com.tvd12.ezyhttp.core.constant.HttpMethod;

public class DeleteRequest<T> extends AbstractRequest<DeleteRequest<T>, T> {

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.DELETE;
	}
	
}
