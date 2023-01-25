package com.tvd12.ezyhttp.server.core.view;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public interface ViewContext {

    void render(
        ServletContext servletContext,
        HttpServletRequest request,
        HttpServletResponse response,
        View view
    ) throws IOException;

    String resolveMessage(
        Locale locale,
        String key,
        Object... parameters
    );
}
