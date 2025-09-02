package com.tvd12.ezyhttp.server.graphql.fetcher;

import com.tvd12.ezyfox.builder.EzyBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static com.tvd12.ezyhttp.server.graphql.util.GraphQLDataFetcherClasses.*;
import static com.tvd12.ezyhttp.server.graphql.util.GraphQLQueryGroupExtractors.extractQueryGroup;

public class GraphQLDataFetcherManager {

    private final Map<String, GraphQLDataFetcher> dataFetchers;
    private final Set<String> authenticatedQueryNames;
    private final Set<String> managementQueryNames;
    private final Set<String> paymentQueryNames;
    private final Map<String, String> groupNameByQueryName;
    private final Map<String, Set<String>> queryNamesByGroupName;

    protected GraphQLDataFetcherManager(Builder builder) {
        this.dataFetchers = builder.dataFetchers;
        this.authenticatedQueryNames = builder.authenticatedQueryNames;
        this.managementQueryNames = builder.managementQueryNames;
        this.paymentQueryNames = builder.paymentQueryNames;
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

    public boolean isAuthenticatedQuery(String queryName) {
        return authenticatedQueryNames.contains(queryName);
    }

    public boolean isManagementQuery(String queryName) {
        return managementQueryNames.contains(queryName);
    }

    public boolean isPaymentQuery(String queryName) {
        return paymentQueryNames.contains(queryName);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<GraphQLDataFetcherManager> {
        private final Map<String, GraphQLDataFetcher> dataFetchers =
            new HashMap<>();
        private final Set<String> authenticatedQueryNames =
            new HashSet<>();
        private final Set<String> managementQueryNames =
            new HashSet<>();
        private final Set<String> paymentQueryNames =
            new HashSet<>();
        private final Map<String, String> groupNameByQueryName =
            new HashMap<>();
        private final Map<String, Set<String>> queryNamesByGroupName =
            new HashMap<>();

        public Builder addDataFetcher(GraphQLDataFetcher fetcher) {
            return addDataFetcher(fetcher.getQueryName(), fetcher);
        }

        public Builder addDataFetcher(
            String queryName,
            GraphQLDataFetcher fetcher
        ) {
            this.dataFetchers.put(queryName, fetcher);
            if (isAuthenticatedFetcher(fetcher)) {
                this.authenticatedQueryNames.add(queryName);
            }
            if (isManagementFetcher(fetcher)) {
                this.managementQueryNames.add(queryName);
            }
            if (isPaymentFetcher(fetcher)) {
                this.paymentQueryNames.add(queryName);
            }
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
