package com.tvd12.ezyhttp.server.core.handler;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.tvd12.ezyhttp.core.constant.HttpMethod;

public interface RequestResponseWatcher {

    default void watchRequest(
            HttpMethod method,
            ServletRequest request
    ) {
        // do nothing
    }

    default void watchResponse(
            HttpMethod method,
            ServletRequest request,
            ServletResponse response
    ) {
        // do nothing
    }
}
