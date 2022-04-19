package com.tvd12.ezyhttp.server.graphql.test.datafetcher;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.graphql.GraphQLAbstractDataFetcher;

@EzySingleton
@SuppressWarnings("rawtypes")
public class GraphQLNoNameDataFetcher
    extends GraphQLAbstractDataFetcher {

    @Override
    public Object getData(Object argument) {
        return "Foo " + argument;
    }
}
