package com.tvd12.ezyhttp.client.request;

import com.tvd12.ezyhttp.core.constant.HttpMethod;

public class PostRequest<T> extends AbstractRequest<PostRequest<T>, T> {
	@Override
	public HttpMethod getMethod() {
		return HttpMethod.POST;
	}
	
}
