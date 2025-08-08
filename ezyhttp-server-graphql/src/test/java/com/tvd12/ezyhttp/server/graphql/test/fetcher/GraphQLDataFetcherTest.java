package com.tvd12.ezyhttp.server.graphql.test.fetcher;

import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class GraphQLDataFetcherTest {

    @Test
    public void test() {
        // given
        ExGraphQLDataFetcher instance = new ExGraphQLDataFetcher();

        // when
        // then
        Asserts.assertNull(instance.getQueryScheme());
        Asserts.assertNull(instance.getResponseScheme());
    }

    private static class ExGraphQLDataFetcher
        implements GraphQLDataFetcher {

        @Override
        public String getData(
            RequestArguments arguments,
            GraphQLQueryDefinition query
        ) {
            return null;
        }
    }
}
