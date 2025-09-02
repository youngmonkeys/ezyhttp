package com.tvd12.ezyhttp.server.graphql.util;

import com.tvd12.ezyhttp.server.graphql.annotation.GraphQLQuery;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public final class GraphQLQueryAnnotations {

    private GraphQLQueryAnnotations() {}

    public static String getQLQueryName(GraphQLQuery annotation) {
        String name = annotation.name();
        if (isBlank(name)) {
            name = annotation.value();
        }
        return name;
    }
}
