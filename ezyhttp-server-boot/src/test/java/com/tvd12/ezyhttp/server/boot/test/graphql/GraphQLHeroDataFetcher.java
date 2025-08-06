package com.tvd12.ezyhttp.server.boot.test.graphql;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLAbstractDataFetcher;


@EzySingleton
public class GraphQLHeroDataFetcher
    extends GraphQLAbstractDataFetcher<Object, int[]> {

    public int[] getData(RequestArguments argument, Object parameter) {
        return new int[]{1, 2, 3};
    }

    @Override
    public String getQueryName() {
        return "hero";
    }
}
