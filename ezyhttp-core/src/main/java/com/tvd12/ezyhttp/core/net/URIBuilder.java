package com.tvd12.ezyhttp.core.net;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.io.EzyStrings;

public class URIBuilder implements EzyBuilder<URI> {

    protected List<String> paths;
    protected Map<String, String> queryParams;
    protected final StringBuilder buidler;

    public URIBuilder() {
        this("");
    }

    public URIBuilder(String str) {
        this.buidler = new StringBuilder(str);
    }

    public URIBuilder addPath(String path) {
        if (!EzyStrings.isEmpty(path)) {
            if (paths == null) {
                this.paths = new ArrayList<>();
            }
            this.paths.add(path);
        }
        return this;
    }

    public URIBuilder addQueryParam(String name, String value) {
        if (queryParams == null) {
            this.queryParams = new HashMap<>();
        }
        this.queryParams.put(name, value);
        return this;
    }

    @Override
    public URI build() {
        String path = buildPath();
        if (path != null) {
            buidler.append(path);
        }
        String query = buildQuery();
        if (query != null) {
            buidler.append("?").append(query);
        }
        return URI.create(buidler.toString());
    }

    protected String buildPath() {
        if (paths == null) {
            return null;
        }
        String path = EzyStrings.join(paths, "/");
        return normalizePath(path);
    }

    protected String buildQuery() {
        if (queryParams == null) {
            return null;
        }
        int index = 0;
        int lastIndex = queryParams.size() - 1;
        StringBuilder b = new StringBuilder();
        for (String name : queryParams.keySet()) {
            String value = queryParams.get(name);
            b.append(name).append("=").append(value);
            if ((index++) < lastIndex) {
                b.append("&");
            }
        }
        return b.toString();
    }

    public static String normalizePath(String path) {
        if (EzyStrings.isEmpty(path)) {
            return "";
        }
        int lastIndex = path.length() - 1;
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < path.length(); ++i) {
            int ch = path.charAt(i);
            if (i < lastIndex) {
                char nextCh = path.charAt(i + 1);
                if (ch == '/' && nextCh == '/') {
                    continue;
                }
            }
            b.append((char) ch);
        }
        return b.toString();
    }
}
