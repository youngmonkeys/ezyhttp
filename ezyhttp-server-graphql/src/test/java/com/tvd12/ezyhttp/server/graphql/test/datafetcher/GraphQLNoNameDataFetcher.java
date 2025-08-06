package com.tvd12.ezyhttp.server.graphql.test.datafetcher;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLAbstractDataFetcher;

@EzySingleton
@SuppressWarnings("rawtypes")
public class GraphQLNoNameDataFetcher
    extends GraphQLAbstractDataFetcher {

    @Override
    public Object getData(
        RequestArguments arguments,
        Object parameter
    ) {
        return "Foo " + parameter;
    }
}
