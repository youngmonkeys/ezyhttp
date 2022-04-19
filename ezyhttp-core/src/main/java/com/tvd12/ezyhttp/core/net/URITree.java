package com.tvd12.ezyhttp.core.net;

import java.util.HashMap;
import java.util.Map;

public class URITree {

    protected String uri;
    protected Map<String, URITree> children;

    public void addURI(String uri) {
        URITree lastChild = this;
        String[] paths = uri.split("/");
        for (String s : paths) {
            if (lastChild.children == null)
                lastChild.children = new HashMap<>();
            String path = s;
            if (PathVariables.isPathVariable(path))
                path = "{}";
            URITree child = lastChild.children.get(path);
            if (child == null) {
                child = new URITree();
                lastChild.children.put(path, child);
            }
            lastChild = child;
        }
        lastChild.uri = uri;
    }

    public String getMatchedURI(String uri) {
        URITree lastChild = this;
        String[] paths = uri.split("/");
        for (String path : paths) {
            if (lastChild.children == null)
                return null;
            URITree child = lastChild.children.get(path);
            if (child == null)
                child = lastChild.children.get("{}");
            if (child == null)
                return null;
            lastChild = child;
        }
        return lastChild.uri;

    }

    @Override
    public String toString() {
        if (children == null)
            return uri;
        return children.toString();

    }

}
