package com.tvd12.ezyhttp.client.request;

import com.tvd12.ezyhttp.core.constant.HttpMethod;

public class PutRequest extends AbstractRequest<PutRequest> {

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.PUT;
	}
	
}
