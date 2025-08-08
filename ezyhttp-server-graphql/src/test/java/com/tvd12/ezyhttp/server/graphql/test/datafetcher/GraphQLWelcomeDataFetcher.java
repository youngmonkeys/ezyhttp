package com.tvd12.ezyhttp.server.graphql.test.datafetcher;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;
import lombok.Data;


@EzySingleton
public class GraphQLWelcomeDataFetcher implements GraphQLDataFetcher {

    public String getData(
        RequestArguments arguments,
        GraphQLQueryDefinition query
    ) {
        return "Welcome " + query.getArgumentValue("name");
    }

    @Override
    public String getQueryName() {
        return "welcome";
    }

    @Data
    public static class WelcomeRequest {
        private String name;
    }
}
