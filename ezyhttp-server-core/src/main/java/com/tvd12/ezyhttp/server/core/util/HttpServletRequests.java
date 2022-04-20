package com.tvd12.ezyhttp.server.core.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;

public final class HttpServletRequests {

    private HttpServletRequests() {
    }

    public static String getRequestValue(HttpServletRequest request, String name) {
        String value = (String) request.getAttribute(name);
        if (value == null) {
            value = request.getHeader(name);
        }
        if (value == null) {
            value = request.getParameter(name);
        }
        if (value == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    value = cookie.getValue();
                    if (isNotBlank(value)) {
                        break;
                    }
                }
            }
        }
        return value;
    }
}
