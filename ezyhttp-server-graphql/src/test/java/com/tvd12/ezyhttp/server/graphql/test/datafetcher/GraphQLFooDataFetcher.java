package com.tvd12.ezyhttp.server.graphql.test.datafetcher;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.annotation.GraphQLQuery;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;


@EzySingleton
@GraphQLQuery(name = "foo")
public class GraphQLFooDataFetcher implements GraphQLDataFetcher {

    @Override
    public Object getData(
        RequestArguments arguments,
        GraphQLQueryDefinition query
    ) {
        String value = query.getFieldArgumentValue(
            "value",
            String.class,
            "value"
        );
        return EzyMapBuilder.mapBuilder()
            .put(
                "value",
                EzyMapBuilder.mapBuilder()
                    .put("bar", value)
                    .toMap()
            )
            .toMap();
    }
}
