package com.tvd12.ezyhttp.server.graphql.fetcher;

import com.tvd12.ezyfox.exception.EzyNotImplementedException;
import com.tvd12.ezyfox.reflect.EzyGenerics;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.annotation.GraphQLQuery;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLDataSchema;

public interface GraphQLDataFetcher<A, D> {

    D getData(
        RequestArguments arguments,
        A parameter
    );

    default String getQueryName() {
        if (getClass().isAnnotationPresent(GraphQLQuery.class)) {
            return getClass().getAnnotation(GraphQLQuery.class).name();
        }
        throw new EzyNotImplementedException("you must implement " +
            getClass().getName() +
            ".getQueryName() method or annotated the class with @GraphQLQuery"
        );
    }

    default Class<?> getParameterType() {
        try {
            Class<?> readerClass = getClass();
            Class<?>[] args = EzyGenerics.getGenericInterfacesArguments(
                readerClass,
                GraphQLDataFetcher.class,
                2
            );
            return args[0];
        } catch (Exception e) {
            return null;
        }
    }

    default GraphQLDataSchema getRequestScheme() {
        return null;
    }

    default GraphQLDataSchema getResponseScheme() {
        return null;
    }
}
