package com.tvd12.ezyhttp.server.graphql;

import com.tvd12.ezyfox.builder.EzyBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static com.tvd12.ezyhttp.core.constant.Constants.DEFAULT_QL_GROUP_NAME;

@SuppressWarnings("rawtypes")
public class GraphQLDataFetcherManager {

    private final Map<String, GraphQLDataFetcher> dataFetchers;
    private final Map<String, Set<String>> queryNamesByGroupName;

    protected GraphQLDataFetcherManager(Builder builder) {
        this.dataFetchers = builder.dataFetchers;
        this.queryNamesByGroupName = builder.queryNamesByGroupName;
    }

    public GraphQLDataFetcher getDataFetcher(String queryName) {
        return dataFetchers.get(queryName);
    }
    
    public Map<String, List<String>> getQueryNameByGroupName() {
        return queryNamesByGroupName.entrySet()
        .stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> new ArrayList<>(entry.getValue())
        ));
    }

    public Map<String, List<String>> getSortedQueryNameByGroupName() {
        return queryNamesByGroupName.entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> {
                    List<String> list = new ArrayList<>(e.getValue());
                    Collections.sort(list);
                    return list;
                },
                (a, b) -> a,
                TreeMap::new
            ));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<GraphQLDataFetcherManager> {

        private final Map<String, GraphQLDataFetcher> dataFetchers = new HashMap<>();
        private final Map<String, Set<String>> queryNamesByGroupName = new HashMap<>();

        public Builder addDataFetcher(Object fetcher) {
            if (fetcher instanceof GraphQLDataFetcher) {
                GraphQLDataFetcher f = (GraphQLDataFetcher) fetcher;
                return addDataFetcher(f.getQueryName(), f);
            }
            return this;
        }

        public Builder addDataFetcher(String queryName, GraphQLDataFetcher fetcher) {
            String group = DEFAULT_QL_GROUP_NAME;
            if (isNotBlank(queryName)) {
                int dotIndex = queryName.indexOf('.');
                if (dotIndex > 0) {
                    group = queryName.substring(0, dotIndex);
                }
            }
            this.dataFetchers.put(queryName, fetcher);
            this.queryNamesByGroupName.computeIfAbsent(
                group,
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
