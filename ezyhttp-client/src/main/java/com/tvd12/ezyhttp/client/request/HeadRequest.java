package com.tvd12.ezyhttp.client.request;

import com.tvd12.ezyhttp.core.constant.HttpMethod;

public class HeadRequest extends AbstractRequest<HeadRequest> {

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.HEAD;
    }
}
