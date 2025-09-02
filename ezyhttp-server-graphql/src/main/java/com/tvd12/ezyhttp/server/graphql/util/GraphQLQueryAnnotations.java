package com.tvd12.ezyhttp.server.graphql.util;

import com.tvd12.ezyhttp.server.graphql.annotation.GraphQLQuery;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static com.tvd12.ezyfox.reflect.EzyClasses.isAnnotationPresentIncludeSuper;

public final class GraphQLQueryAnnotations {

    private GraphQLQueryAnnotations() {}

    public static String getQLQueryName(
        GraphQLQuery annotation
    ) {
        String name = annotation.name();
        if (isBlank(name)) {
            name = annotation.value();
        }
        return name;
    }

    public static String getQLQueryName(
        Class<?> dataFetcherClazz
    ) {
        if (isAnnotationPresentIncludeSuper(
            dataFetcherClazz,
            GraphQLQuery.class
        )) {
            return getQLQueryName(
                dataFetcherClazz.getAnnotation(GraphQLQuery.class)
            );
        }
        return null;
    }
}
