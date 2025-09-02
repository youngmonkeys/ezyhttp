package com.tvd12.ezyhttp.server.graphql.fetcher;

import com.tvd12.ezyfox.exception.EzyNotImplementedException;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLDataSchema;

import java.util.List;

import static com.tvd12.ezyhttp.server.graphql.util.GraphQLQueryAnnotations.getQLQueryGroupName;
import static com.tvd12.ezyhttp.server.graphql.util.GraphQLQueryAnnotations.getQLQueryName;

public interface GraphQLDataFetcher {

    Object getData(
        RequestArguments arguments,
        GraphQLQueryDefinition query
    );

    default String getQueryName() {
        String queryName = getQLQueryName(getClass());
        if (queryName != null) {
            return queryName;
        }
        throw new EzyNotImplementedException("you must implement " +
            getClass().getName() +
            ".getQueryName() method or annotated the class with @GraphQLQuery"
        );
    }

    default String getQueryGroupName() {
        return getQLQueryGroupName(getClass());
    }

    default List<GraphQLDataSchema> getQueryScheme() {
        return null;
    }

    default GraphQLDataSchema getResponseScheme() {
        return null;
    }
}
