package com.tvd12.ezyhttp.server.graphql.interceptor;

import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;

public interface GraphQLInterceptor {

    boolean preHandle(
        RequestArguments arguments,
        String queryGroup,
        String queryName,
        Object requestParameter,
        GraphQLDataFetcher<?, ?> dataFetcher
    );

    void postHandle(
        RequestArguments arguments,
        String queryGroup,
        String queryName,
        Object responseData,
        GraphQLDataFetcher<?, ?> dataFetcher
    );

    int getPriority();
}
