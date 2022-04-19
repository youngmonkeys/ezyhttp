package com.tvd12.ezyhttp.server.graphql.test.datafetcher;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.graphql.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.annotation.GraphQLQuery;


@SuppressWarnings("rawtypes")
@EzySingleton
@GraphQLQuery(name = "foo")
public class GraphQLFooDataFetcher implements GraphQLDataFetcher {

    @Override
    public Object getData(Object argument) {
        return "Foo " + argument;
    }
}
