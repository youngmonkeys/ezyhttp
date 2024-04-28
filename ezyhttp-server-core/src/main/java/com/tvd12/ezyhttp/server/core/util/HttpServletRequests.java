package com.tvd12.ezyhttp.server.core.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;

public final class HttpServletRequests {

    private HttpServletRequests() {}

    /**
     * Get request value from attribute or header or parameter or cookie.
     *
     * @param request the http request.
     * @param name the name of value.
     * @return the request value.
     */
    public static String getRequestValue(
        HttpServletRequest request,
        String name
    ) {
        String value = (String) request.getAttribute(name);
        if (isBlank(value)) {
            value = request.getHeader(name);
        }
        if (isBlank(value)) {
            value = request.getParameter(name);
        }
        if (isBlank(value) && request.getCookies() != null) {
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

    /**
     * Get request value from attribute or header or parameter or cookie.
     * If the value is blank, try to get by lowercase of name.
     *
     * @param request the http request.
     * @param name the name of value.
     * @return the request value.
     */
    public static String getRequestValueAnyway(
        HttpServletRequest request,
        String name
    ) {
        String value = getRequestValue(request, name);
        if (isBlank(value)) {
            value = getRequestValue(request, name.toLowerCase());
        }
        return value;
    }
}
