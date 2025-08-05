package com.tvd12.ezyhttp.server.graphql;

import com.tvd12.ezyfox.exception.EzyNotImplementedException;
import com.tvd12.ezyfox.reflect.EzyGenerics;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.annotation.GraphQLQuery;

public interface GraphQLDataFetcher<A, D> {

    D getData(
        RequestArguments arguments,
        A parameters
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

    default Class<?> getArgumentType() {
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
}
