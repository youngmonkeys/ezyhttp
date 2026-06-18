package com.tvd12.ezyhttp.server.graphql.fetcher;

public interface GraphQLDataFetcherProvider {

    GraphQLDataFetcher provide(String queryName);
}
