package com.tvd12.ezyhttp.server.core.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.AsyncContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.security.EzyBase64;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.net.PathVariables;
import com.tvd12.ezyhttp.server.core.constant.CoreConstants;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class SimpleRequestArguments implements RequestArguments {

    @Setter
    protected boolean debug;
    @Setter
    @Getter
    protected HttpMethod method;
    @Setter
    @Getter
    protected String uriTemplate;
    @Setter
    protected ObjectMapper objectMapper;
    @Getter
    protected HttpServletRequest request;
    @Getter
    protected HttpServletResponse response;
    protected Map<Object, Object> arguments;
    protected List<String> headerList;
    protected Map<String, String> headerMap;
    protected List<String> parameterList;
    protected Map<String, String> parameterMap;
    protected Map<String, String> pathVariableMap;
    protected List<Entry<String, String>> pathVariableList;
    protected Cookie[] cookies;
    protected Map<String, Cookie> cookieMap;
    @Getter
    protected Map<String, Object> redirectionAttributes;

    private static final Logger LOGGER =
        LoggerFactory.getLogger(SimpleRequestArguments.class);

    @Override
    public <T> T getArgument(Object key) {
        Object argument = arguments != null ? arguments.get(key) : null;
        if (argument == null && debug) {
            LOGGER.error("there is no value for argment: {}", key);
        }
        return (T) argument;
    }

    @Override
    public void setArgument(Object key, Object value) {
        if (arguments == null) {
            arguments = new HashMap<>();
        }
        arguments.put(key, value);
    }

    @Override
    public String getParameter(int index) {
        if (parameterList == null) {
            return null;
        }
        if (parameterList.size() <= index) {
            return null;
        }
        return parameterList.get(index);
    }

    @Override
    public String getParameter(String name) {
        if (parameterMap == null) {
            return null;
        }
        return parameterMap.get(name);
    }

    @Override
    public Map<String, String> getParameters() {
        return parameterMap;
    }

    public void setParameter(String name, String[] values) {
        if (values == null) {
            return;
        }
        if (parameterList == null) {
            parameterList = new ArrayList<>();
        }
        if (parameterMap == null) {
            parameterMap = new HashMap<>();
        }
        String value = values.length == 0
            ? ""
            : values.length == 1 ? values[0] : String.join(",", values);
        parameterList.add(value);
        parameterMap.put(name, value);
    }

    @Override
    public String getHeader(int index) {
        if (headerList == null) {
            return null;
        }
        if (headerList.size() <= index) {
            return null;
        }
        return headerList.get(index);
    }

    @Override
    public String getHeader(String name) {
        if (headerMap == null) {
            return null;
        }
        return headerMap.get(name);
    }

    public void setHeader(String name, String value) {
        if (headerList == null) {
            headerList = new ArrayList<>();
        }
        if (headerMap == null) {
            headerMap = new HashMap<>();
        }
        headerList.add(value);
        headerMap.put(name, value);
    }

    @Override
    public String getPathVariable(int index) {
        fetchPathVariables();
        if (pathVariableList.size() <= index) {
            return null;
        }
        return pathVariableList.get(index).getValue();
    }

    @Override
    public String getPathVariable(String name) {
        fetchPathVariables();
        return pathVariableMap.get(name);
    }

    protected void fetchPathVariables() {
        if (pathVariableList == null) {
            pathVariableList = PathVariables.getVariables(
                uriTemplate,
                request.getRequestURI()
            );
            pathVariableMap = new HashMap<>();
            for (Entry<String, String> entry : pathVariableList) {
                pathVariableMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
        this.setArgument(HttpServletRequest.class, request);
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
        this.setArgument(HttpServletResponse.class, response);
    }

    @Override
    public String getContentType() {
        return ContentTypes.getContentType(request.getContentType());
    }

    @Override
    public String getRequestContentType() {
        return request.getContentType();
    }

    @Override
    public int getContentLength() {
        return request.getContentLength();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    public void setCookies(Cookie[] cookies) {
        if (cookies == null || cookies.length == 0) {
            return;
        }
        this.cookies = cookies;
        this.cookieMap = new HashMap<>();
        for (Cookie cookie : cookies) {
            Cookie old = cookieMap.get(cookie.getName());
            if (old == null || EzyStrings.isBlank(old.getValue())) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
    }

    @Override
    public String getCookieValue(int index) {
        if (cookies == null || cookies.length <= index) {
            return null;
        }
        String cookieName = cookies[index].getName();
        Cookie cookie = cookieMap.get(cookieName);
        return cookie.getValue();
    }

    @Override
    public String getCookieValue(String name) {
        Cookie cookie = getCookie(name);
        return cookie != null ? cookie.getValue() : null;
    }

    @Override
    public Cookie getCookie(String name) {
        return cookieMap != null ? cookieMap.get(name) : null;
    }

    public void setRedirectionAttributesFromCookie() {
        Cookie cookie = getCookie(CoreConstants.COOKIE_REDIRECT_ATTRIBUTES_NAME);
        if (cookie == null) {
            return;
        }
        try {
            String value = EzyBase64.decodeUtf(cookie.getValue());
            this.redirectionAttributes = objectMapper.readValue(value, Map.class);
        } catch (Exception e) {
            // do nothing
        }
        Cookie newCookie = new Cookie(CoreConstants.COOKIE_REDIRECT_ATTRIBUTES_NAME, "");
        newCookie.setMaxAge(0);
        response.addCookie(newCookie);
    }

    @Override
    public <T> T getRedirectionAttribute(String name) {
        if (redirectionAttributes == null) {
            return null;
        }
        return (T) redirectionAttributes.get(name);
    }

    @Override
    public <T> T getRedirectionAttribute(String name, Class<T> outType) {
        Object value = getRedirectionAttribute(name);
        return value != null ? objectMapper.convertValue(value, outType) : null;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return request.getAsyncContext();
    }

    @Override
    public boolean isAsyncStarted() {
        return request.isAsyncStarted();
    }

    @Override
    public void release() {
        if (arguments != null) {
            arguments.clear();
        }
        if (headerList != null) {
            headerList.clear();
        }
        if (headerMap != null) {
            headerMap.clear();
        }
        if (parameterList != null) {
            parameterList.clear();
        }
        if (parameterMap != null) {
            parameterMap.clear();
        }
        if (cookieMap != null) {
            cookieMap.clear();
        }
        if (pathVariableList != null) {
            pathVariableList.clear();
        }
        if (pathVariableMap != null) {
            pathVariableMap.clear();
        }
        if (redirectionAttributes != null) {
            redirectionAttributes.clear();
        }
        this.arguments = null;
        this.headerList = null;
        this.headerMap = null;
        this.parameterList = null;
        this.parameterMap = null;
        this.cookies = null;
        this.cookieMap = null;
        this.pathVariableList = null;
        this.pathVariableMap = null;
        this.request = null;
        this.response = null;
        this.objectMapper = null;
        this.redirectionAttributes = null;
    }
}
