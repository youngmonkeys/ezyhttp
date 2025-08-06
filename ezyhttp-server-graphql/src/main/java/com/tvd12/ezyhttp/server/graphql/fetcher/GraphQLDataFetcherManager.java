package com.tvd12.ezyhttp.server.graphql.fetcher;

import com.tvd12.ezyfox.builder.EzyBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static com.tvd12.ezyhttp.server.graphql.util.GraphQLQueryGroupExtractors.extractQueryGroup;

@SuppressWarnings("rawtypes")
public class GraphQLDataFetcherManager {

    private final Map<String, GraphQLDataFetcher> dataFetchers;
    private final Map<String, String> groupNameByQueryName;
    private final Map<String, Set<String>> queryNamesByGroupName;

    protected GraphQLDataFetcherManager(Builder builder) {
        this.dataFetchers = builder.dataFetchers;
        this.groupNameByQueryName = builder.groupNameByQueryName;
        this.queryNamesByGroupName = builder.queryNamesByGroupName;
    }

    public String getGroupNameByQueryName(String queryName) {
        return groupNameByQueryName.get(queryName);
    }

    public GraphQLDataFetcher getDataFetcher(String queryName) {
        return dataFetchers.get(queryName);
    }
    
    public Map<String, List<String>> getQueryNameByGroupName() {
        return queryNamesByGroupName.entrySet()
        .stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry -> new ArrayList<>(entry.getValue())
            )
        );
    }

    public Map<String, List<String>> getSortedQueryNameByGroupName() {
        Map<String, List<String>> result = new TreeMap<>();
        for (Map.Entry<String, Set<String>> entry : queryNamesByGroupName.entrySet()) {
            List<String> list = new ArrayList<>(entry.getValue());
            Collections.sort(list);
            result.put(entry.getKey(), list);
        }
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<GraphQLDataFetcherManager> {

        private final Map<String, GraphQLDataFetcher> dataFetchers =
            new HashMap<>();
        private final Map<String, String> groupNameByQueryName =
            new HashMap<>();
        private final Map<String, Set<String>> queryNamesByGroupName =
            new HashMap<>();

        public Builder addDataFetcher(Object fetcher) {
            if (fetcher instanceof GraphQLDataFetcher) {
                GraphQLDataFetcher f = (GraphQLDataFetcher) fetcher;
                return addDataFetcher(f.getQueryName(), f);
            }
            return this;
        }

        public Builder addDataFetcher(
            String queryName,
            GraphQLDataFetcher fetcher
        ) {
            this.dataFetchers.put(queryName, fetcher);
            String groupName = extractQueryGroup(queryName);
            this.groupNameByQueryName.put(queryName, groupName);
            this.queryNamesByGroupName.computeIfAbsent(
                groupName,
                k -> new HashSet<>()
            ).add(queryName);
            return this;
        }

        @Override
        public GraphQLDataFetcherManager build() {
            return new GraphQLDataFetcherManager(this);
        }
    }
}
