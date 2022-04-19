package com.tvd12.ezyhttp.server.boot.test.graphql;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.graphql.GraphQLAbstractDataFetcher;


@EzySingleton
public class GraphQLHeroDataFetcher
    extends GraphQLAbstractDataFetcher<Object, int[]> {

    public int[] getData(Object argument) {
        return new int[]{1, 2, 3};
    }

    @Override
    public String getQueryName() {
        return "hero";
    }
}
