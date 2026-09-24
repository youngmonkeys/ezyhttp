package com.tvd12.ezyhttp.server.core.request;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import lombok.Getter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.Part;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public class DeferredMultipartHttpServletRequest
    extends HttpServletRequestWrapper {

    @Getter
    private volatile boolean contentAccessible;
    private final Map<String, String[]> queryParameters;

    public DeferredMultipartHttpServletRequest(
        HttpServletRequest request
    ) {
        super(request);
        this.queryParameters = decodeQueryString(
            request.getQueryString()
        );
    }

    public static boolean isMultipartRequest(
        HttpServletRequest request
    ) {
        String contentType = ContentTypes.getContentType(
            request.getContentType()
        );
        return contentType != null
            && ContentTypes.MULTIPART_FORM_DATA.equalsIgnoreCase(
                contentType.trim()
            );
    }

    public void allowContentAccess() {
        this.contentAccessible = true;
    }

    @Override
    public String getParameter(String name) {
        if (contentAccessible) {
            return super.getParameter(name);
        }
        String[] values = queryParameters.get(name);
        return values != null ? values[0] : null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        if (contentAccessible) {
            return super.getParameterNames();
        }
        return Collections.enumeration(queryParameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        if (contentAccessible) {
            return super.getParameterValues(name);
        }
        String[] values = queryParameters.get(name);
        return values != null ? values.clone() : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (contentAccessible) {
            return super.getParameterMap();
        }
        Map<String, String[]> answer = new LinkedHashMap<>();
        for (Entry<String, String[]> e : queryParameters.entrySet()) {
            answer.put(e.getKey(), e.getValue().clone());
        }
        return Collections.unmodifiableMap(answer);
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        checkContentAccessible();
        return super.getParts();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        checkContentAccessible();
        return super.getPart(name);
    }

    private void checkContentAccessible() {
        if (!contentAccessible) {
            throw new IllegalStateException(
                "multipart content is not accessible before " +
                    "the request is accepted"
            );
        }
    }

    private static Map<String, String[]> decodeQueryString(
        String queryString
    ) {
        Map<String, List<String>> valuesByName = new LinkedHashMap<>();
        if (!isBlank(queryString)) {
            for (String keyValue : queryString.split("&")) {
                int index = keyValue.indexOf('=');
                String name = decode(
                    index < 0 ? keyValue : keyValue.substring(0, index)
                );
                if (name.isEmpty()) {
                    continue;
                }
                String value = index < 0
                    ? EMPTY_STRING
                    : decode(keyValue.substring(index + 1));
                valuesByName
                    .computeIfAbsent(name, k -> new ArrayList<>())
                    .add(value);
            }
        }
        Map<String, String[]> answer = new LinkedHashMap<>();
        for (Entry<String, List<String>> e : valuesByName.entrySet()) {
            answer.put(e.getKey(), e.getValue().toArray(new String[0]));
        }
        return answer;
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, EzyStrings.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }
}
