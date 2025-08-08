package com.tvd12.ezyhttp.server.graphql.test.datafetcher;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLAbstractDataFetcher;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;

@EzySingleton
public class GraphQLNoNameDataFetcher
    extends GraphQLAbstractDataFetcher {

    @Override
    public Object getData(
        RequestArguments arguments,
        GraphQLQueryDefinition query
    ) {
        return "Foo " + query;
    }
}
