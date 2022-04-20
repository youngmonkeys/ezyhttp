package com.tvd12.ezyhttp.server.core.view;

import javax.servlet.http.HttpServletRequest;

public interface ViewDecorator {

    void decorate(HttpServletRequest request, View view);

    default int getPriority() {
        return 0;
    }
}
