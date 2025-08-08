package com.tvd12.ezyhttp.server.graphql.test.config;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.annotation.GraphQLQuery;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;

@EzySingleton
@GraphQLQuery(name = "A")
public class GraphQLADataFetcher implements GraphQLDataFetcher {
    @Override
    public Object getData(
        RequestArguments arguments,
        GraphQLQueryDefinition query
    ) {
        return "A";
    }
}
