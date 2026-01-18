package com.tvd12.ezyhttp.server.core.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.tvd12.ezyfox.io.EzyStrings.*;

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
        return getRequestValue(request, name, Boolean.TRUE);
    }

    /**
     * Get request value from attribute or header or parameter or cookie.
     *
     * @param request the http request.
     * @param name the name of value.
     * @param checkCookie check request value in cookie or not
     * @return the request value.
     */
    public static String getRequestValue(
        HttpServletRequest request,
        String name,
        boolean checkCookie
    ) {
        String value = (String) request.getAttribute(name);
        if (isBlank(value)) {
            value = request.getHeader(name);
        }
        if (isBlank(value)) {
            value = request.getParameter(name);
        }
        if (isBlank(value) && checkCookie) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(name)) {
                        value = cookie.getValue();
                        if (isNotBlank(value)) {
                            break;
                        }
                    }
                }
            }
        }
        return value;
    }

    /**
     * Get request value from attribute or header or parameter or cookie.
     * If the value is blank, try to get by lowercase of name.
     * If the value is still blank, try retrieving it using
     * the name with the first letter capitalized.
     *
     * @param request the http request.
     * @param name the name of value.
     * @return the request value.
     */
    public static String getRequestValueAnyway(
        HttpServletRequest request,
        String name
    ) {
        return getRequestValueAnyway(request, name, Boolean.TRUE);
    }

    /**
     * Get request value from attribute or header or parameter or cookie.
     * If the value is blank, try to get by lowercase of name.
     * If the value is still blank, try retrieving it using
     * the name with the first letter capitalized.
     *
     * @param request the http request.
     * @param name the name of value.
     * @return the request value.
     */
    public static String getRequestValueAnyway(
        HttpServletRequest request,
        String name,
        boolean checkCookie
    ) {
        String value = getRequestValue(request, name, checkCookie);
        String argumentNameLowerCase = EMPTY_STRING;
        if (isBlank(value)) {
            argumentNameLowerCase = name.toLowerCase();
            value = getRequestValue(
                request,
                argumentNameLowerCase,
                checkCookie
            );
        }
        if (isBlank(value)) {
            String argumentNameFirstUpperCase =
                argumentNameLowerCase.substring(0, 1).toUpperCase() +
                argumentNameLowerCase.substring(1);
            value = getRequestValue(
                request,
                argumentNameFirstUpperCase,
                checkCookie
            );
        }
        return value;
    }
}
