package com.tvd12.ezyhttp.server.graphql.test.datafetcher;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.graphql.GraphQLAbstractDataFetcher;


@EzySingleton
public class GraphQLHeroDataFetcher
    extends GraphQLAbstractDataFetcher<Object, String> {

    public String getData(Object argument) {
        return "Hero 007";
    }

    @Override
    public String getQueryName() {
        return "hero";
    }
}
