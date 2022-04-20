package com.tvd12.ezyhttp.server.core.handler;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

public interface UncaughtExceptionHandler {

    Object handleException(
            RequestArguments arguments,
            Exception exception
    ) throws Exception;

    default String getResponseContentType() {
        return ContentTypes.APPLICATION_JSON;
    }
}
