package com.tvd12.ezyhttp.server.core.handler;

import static com.tvd12.ezyhttp.core.constant.Constants.DEFAULT_URI;

public interface IRequestController {

    default String getDefaultUri() {
        return DEFAULT_URI;
    }
}
