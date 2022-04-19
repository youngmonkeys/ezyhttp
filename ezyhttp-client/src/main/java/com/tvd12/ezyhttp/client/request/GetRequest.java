package com.tvd12.ezyhttp.client.request;

import com.tvd12.ezyhttp.core.constant.HttpMethod;

public class GetRequest extends AbstractRequest<GetRequest> {

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
    
}
