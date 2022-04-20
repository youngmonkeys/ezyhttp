package com.tvd12.ezyhttp.server.core.request;

import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyfox.util.EzyReleasable;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.data.BodyData;

public interface RequestArguments extends BodyData, EzyReleasable {

    HttpMethod getMethod();

    HttpServletRequest getRequest();

    HttpServletResponse getResponse();

    String getUriTemplate();

    AsyncContext getAsyncContext();

    boolean isAsyncStarted();

    <T> T getArgument(Object key);

    void setArgument(Object key, Object value);

    String getParameter(int index);

    String getParameter(String name);

    default String getParameter(int index, String defaultValue) {
        String answer = getParameter(index);
        return answer != null ? answer : defaultValue;
    }

    default String getParameter(String name, String defaultValue) {
        String answer = getParameter(name);
        return answer != null ? answer : defaultValue;
    }

    String getHeader(int index);

    String getHeader(String name);

    default String getHeader(String name, String defaultValue) {
        String answer = getHeader(name);
        return answer != null ? answer : defaultValue;
    }

    default String getHeader(int index, String defaultValue) {
        String answer = getHeader(index);
        return answer != null ? answer : defaultValue;
    }

    String getPathVariable(int index);

    String getPathVariable(String name);

    Cookie getCookie(String name);

    String getCookieValue(int index);

    String getCookieValue(String name);

    default String getCookieValue(int index, String defaultValue) {
        String answer = getCookieValue(index);
        return answer != null ? answer : defaultValue;
    }

    default String getCookieValue(String name, String defaultValue) {
        String answer = getCookieValue(name);
        return answer != null ? answer : defaultValue;
    }

    Map<String, Object> getRedirectionAttributes();

    <T> T getRedirectionAttribute(String name);

    <T> T getRedirectionAttribute(String name, Class<T> outType);

    default <T> T getRedirectionAttribute(String name, T defaultValue) {
        T value = getRedirectionAttribute(name);
        return value != null ? value : defaultValue;
    }

    default <T> T getRedirectionAttribute(String name, Class<T> outType, T defaultValue) {
        T value = getRedirectionAttribute(name, outType);
        return value != null ? value : defaultValue;
    }
}
