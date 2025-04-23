package com.tvd12.ezyhttp.core.net;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class URITree {

    protected String uri;
    protected Map<String, URITree> children;

    public void addURI(String uri) {
        URITree lastChild = this;
        String[] paths = uri.split("/");
        for (String s : paths) {
            if (lastChild.children == null) {
                lastChild.children = new HashMap<>();
            }
            String path = s;
            if (PathVariables.isPathVariable(path)) {
                path = "{}";
            }
            lastChild = lastChild
                .children
                .computeIfAbsent(path, k -> new URITree());
        }
        lastChild.uri = uri;
    }

    public String getMatchedURI(String uri) {
        String[] paths = uri.split("/");
        Queue<TreeItem> queue = new LinkedList<>();
        queue.offer(new TreeItem(0, this));
        while (true) {
            TreeItem item = queue.poll();
            if (item == null) {
                return null;
            }
            if (item.index == paths.length) {
                if (item.child.uri != null) {
                    return item.child.uri;
                }
                continue;
            }
            if (item.child.children == null) {
                continue;
            }
            URITree childA = item.child.children.get(paths[item.index]);
            if (childA != null) {
                queue.offer(new TreeItem(item.index + 1, childA));
            }
            URITree childB = item.child.children.get("{}");
            if (childB != null) {
                queue.offer(new TreeItem(item.index + 1, childB));
            }
            URITree childC = item.child.children.get("*");
            if (childC != null) {
                return childC.uri;
            }
        }
    }

    @Override
    public String toString() {
        if (children == null) {
            return uri;
        }
        return children.toString();
    }

    private static class TreeItem {
        private final int index;
        private final URITree child;

        private TreeItem(int index, URITree child) {
            this.index = index;
            this.child = child;
        }
    }
}
