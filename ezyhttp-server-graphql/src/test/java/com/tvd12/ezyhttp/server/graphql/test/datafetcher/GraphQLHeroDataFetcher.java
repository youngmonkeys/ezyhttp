package com.tvd12.ezyhttp.server.graphql.test.datafetcher;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLAbstractDataFetcher;


@EzySingleton
public class GraphQLHeroDataFetcher
    extends GraphQLAbstractDataFetcher<Object, String> {

    public String getData(
        RequestArguments arguments,
        Object parameter
    ) {
        return "Hero 007";
    }

    @Override
    public String getQueryName() {
        return "hero";
    }
}
