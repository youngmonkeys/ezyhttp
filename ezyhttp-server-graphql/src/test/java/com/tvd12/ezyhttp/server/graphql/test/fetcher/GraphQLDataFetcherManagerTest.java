package com.tvd12.ezyhttp.server.graphql.test.fetcher;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcherManager;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.testng.collections.Sets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GraphQLDataFetcherManagerTest {

    @Test
    public void test() {
        // given
        GraphQLDataFetcherManager instance = GraphQLDataFetcherManager.builder()
            .addDataFetcher(new Fetcher1())
            .addDataFetcher(new Fetcher11())
            .addDataFetcher(new Fetcher2())
            .addDataFetcher(new Fetcher22())
            .build();

        // when
        Map<String, List<String>> queryNameByGroupName = instance
            .getQueryNameByGroupName();

        Map<String, List<String>> sortedQueryNameByGroupName = instance
            .getSortedQueryNameByGroupName();

        // then
        Asserts.assertEquals(
            queryNameByGroupName,
            EzyMapBuilder.mapBuilder()
                .put("core1", new ArrayList<>(Sets.newHashSet("core1.fetcher1", "core1.fetcher11")))
                .put("core2", new ArrayList<>(Sets.newHashSet("core2.fetcher2", "core2.fetcher22")))
                .toMap(),
            false
        );

        Asserts.assertEquals(
            sortedQueryNameByGroupName,
            EzyMapBuilder.mapBuilder()
                .put("core1", Arrays.asList("core1.fetcher1", "core1.fetcher11"))
                .put("core2", Arrays.asList("core2.fetcher2", "core2.fetcher22"))
                .toMap(),
            false
        );
    }

    private static class Fetcher1 implements GraphQLDataFetcher<String, String> {

        @Override
        public String getData(
            RequestArguments arguments,
            String parameter
        ) {
            return null;
        }

        @Override
        public String getQueryName() {
            return "core1.fetcher1";
        }
    }

    private static class Fetcher11 implements GraphQLDataFetcher<String, String> {

        @Override
        public String getData(
            RequestArguments arguments,
            String parameter
        ) {
            return null;
        }

        @Override
        public String getQueryName() {
            return "core1.fetcher11";
        }
    }

    private static class Fetcher2 implements GraphQLDataFetcher<String, String> {

        @Override
        public String getData(
            RequestArguments arguments,
            String parameter
        ) {
            return null;
        }

        @Override
        public String getQueryName() {
            return "core2.fetcher2";
        }
    }

    private static class Fetcher22 implements GraphQLDataFetcher<String, String> {

        @Override
        public String getData(
            RequestArguments arguments,
            String parameter
        ) {
            return null;
        }

        @Override
        public String getQueryName() {
            return "core2.fetcher22";
        }
    }
}
