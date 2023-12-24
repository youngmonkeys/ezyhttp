package com.tvd12.ezyhttp.client.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.Headers;
import com.tvd12.ezyhttp.core.data.MultiValueMap;

import lombok.Getter;

@Getter
public class RequestEntity {

    protected final Object body;
    protected final MultiValueMap headers;

    public RequestEntity(MultiValueMap headers, Object body) {
        this.body = body;
        this.headers = headers;
    }

    public RequestEntity(Map<String, List<String>> headers, Object body) {
        this(headers != null ? new MultiValueMap(headers) : null, body);
    }

    public static Builder of(Object body) {
        return builder().body(body);
    }

    public static RequestEntity body(Object body) {
        return new RequestEntity((MultiValueMap) null, body);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBody() {
        return (T) body;
    }

    public String getHeader(String name) {
        if (headers == null) {
            return null;
        }
        return headers.getValue(name);
    }

    public String getContentType() {
        if (headers == null) {
            return ContentTypes.APPLICATION_JSON;
        }
        return headers.getValue(Headers.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
    }

    @Override
    public String toString() {
        return "RequestEntity(" +
            "headers: " + headers + ", " +
            "body: " + (body != null ? body.getClass().getSimpleName() : "null") +
            ")";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<RequestEntity> {

        protected Object body;
        protected Map<String, List<String>> headers;

        public Builder body(Object body) {
            if (body != null) {
                if (body instanceof MultiValueMap) {
                    this.body = ((MultiValueMap) body).toMap();
                } else {
                    this.body = body;
                }
            }
            return this;
        }

        public Builder header(String name, String value) {
            if (value == null) {
                return this;
            }
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            headers
                .computeIfAbsent(name, k -> new ArrayList<>())
                .add(value);
            return this;
        }

        public Builder header(String name, List<String> values) {
            for (String value : values) {
                header(name, value);
            }
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            for (Entry<String, String> header : headers.entrySet()) {
                header(header.getKey(), header.getValue());
            }
            return this;
        }

        public Builder accept(String contentEncoding) {
            return header(Headers.ACCEPT_ENCODING, contentEncoding);
        }

        public Builder contentType(String contentType) {
            return header(Headers.CONTENT_TYPE, contentType);
        }

        @Override
        public RequestEntity build() {
            return new RequestEntity(headers, body);
        }
    }
}
