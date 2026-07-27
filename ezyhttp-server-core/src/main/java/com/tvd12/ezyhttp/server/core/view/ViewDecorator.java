package com.tvd12.ezyhttp.server.core.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ViewDecorator {

    default void decorate(
        HttpServletRequest request,
        HttpServletResponse response,
        View view
    ) {
        decorate(request, view);
    }

    default void decorate(
        HttpServletRequest request,
        View view
    ) {}

    default int getPriority() {
        return 0;
    }
}
