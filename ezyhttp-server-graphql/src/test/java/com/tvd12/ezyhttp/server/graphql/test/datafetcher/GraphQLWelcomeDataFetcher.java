package com.tvd12.ezyhttp.server.graphql.test.datafetcher;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.test.datafetcher.GraphQLWelcomeDataFetcher.WelcomeRequest;
import lombok.Data;


@EzySingleton
public class GraphQLWelcomeDataFetcher implements GraphQLDataFetcher<WelcomeRequest, String> {

    public String getData(
        RequestArguments arguments,
        WelcomeRequest parameter
    ) {
        return "Welcome " + parameter.getName();
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
