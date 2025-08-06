package com.tvd12.ezyhttp.server.graphql.test.config;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.annotation.GraphQLQuery;

@SuppressWarnings("rawtypes")
@EzySingleton
@GraphQLQuery(name = "A")
public class GraphQLADataFetcher implements GraphQLDataFetcher {
    @Override
    public Object getData(
        RequestArguments arguments,
        Object parameter
    ) {
        return "A";
    }
}
