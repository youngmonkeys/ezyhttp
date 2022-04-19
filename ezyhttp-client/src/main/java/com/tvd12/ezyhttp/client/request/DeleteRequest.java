package com.tvd12.ezyhttp.client.request;

import com.tvd12.ezyhttp.core.constant.HttpMethod;

public class DeleteRequest extends AbstractRequest<DeleteRequest> {

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.DELETE;
    }
}
