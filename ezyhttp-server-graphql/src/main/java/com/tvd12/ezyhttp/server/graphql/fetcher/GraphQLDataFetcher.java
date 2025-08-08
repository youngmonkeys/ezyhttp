package com.tvd12.ezyhttp.server.graphql.fetcher;

import com.tvd12.ezyfox.exception.EzyNotImplementedException;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.annotation.GraphQLQuery;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLDataSchema;

import java.util.List;

public interface GraphQLDataFetcher {

    Object getData(
        RequestArguments arguments,
        GraphQLQueryDefinition query
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

    default List<GraphQLDataSchema> getQueryScheme() {
        return null;
    }

    default GraphQLDataSchema getResponseScheme() {
        return null;
    }
}
