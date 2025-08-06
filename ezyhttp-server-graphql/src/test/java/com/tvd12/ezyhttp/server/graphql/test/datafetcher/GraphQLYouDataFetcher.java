package com.tvd12.ezyhttp.server.graphql.test.datafetcher;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLAbstractDataFetcher;
import com.tvd12.ezyhttp.server.graphql.annotation.GraphQLQuery;
import lombok.Builder;
import lombok.Getter;

@EzySingleton
@GraphQLQuery(name = "you")
public class GraphQLYouDataFetcher
    extends GraphQLAbstractDataFetcher<Object, GraphQLYouDataFetcher.YouResponse> {

    public YouResponse getData(
        RequestArguments arguments,
        Object argument
    ) {
        return YouResponse.builder()
            .friends("A, B")
            .build();
    }

    @Getter
    @Builder
    public static class YouResponse {
        private String friends;
    }
}
