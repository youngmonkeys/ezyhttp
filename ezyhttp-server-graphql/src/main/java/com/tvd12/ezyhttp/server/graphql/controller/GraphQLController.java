package com.tvd12.ezyhttp.server.graphql.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.core.exception.HttpNotAcceptableException;
import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import com.tvd12.ezyhttp.server.core.annotation.*;
import com.tvd12.ezyhttp.server.core.handler.AuthenticatedController;
import com.tvd12.ezyhttp.server.core.handler.IRequestController;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLDataFilter;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLField;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLRequest;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLObjectMapperException;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcherManager;
import com.tvd12.ezyhttp.server.graphql.interceptor.GraphQLInterceptor;
import com.tvd12.ezyhttp.server.graphql.interceptor.GraphQLInterceptorManager;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchema;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchemaParser;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static java.util.Collections.singletonMap;

@Api
@Authenticatable
public class GraphQLController
    implements IRequestController, AuthenticatedController {

    @Getter
    private final boolean authenticated;
    private final ObjectMapper objectMapper;
    private final GraphQLSchemaParser schemaParser;
    private final GraphQLDataFilter dataFilter;
    private final GraphQLDataFetcherManager dataFetcherManager;
    private final GraphQLInterceptorManager interceptorManager;

    public GraphQLController(Builder builder) {
        this.authenticated = builder.authenticated;
        this.objectMapper = builder.objectMapper;
        this.schemaParser = builder.schemaParser;
        this.dataFilter = builder.dataFilter;
        this.dataFetcherManager = builder.dataFetcherManager;
        this.interceptorManager = builder.interceptorManager;
    }

    /**
     * Follow by this suggestion: <a href="https://graphql.org/learn/serving-over-http/">https://graphql.org/learn/serving-over-http/</a>.
     * Example:
     * <code>
     * curl --location -g --request GET 'http://localhost:8083/graphql?query={me{myVariable: $someValue}{id+name+friends{name}}}&amp;variables={"id" : 1}'
     * </code>
     *
     * @param arguments the request arguments
     * @param query     GraphQL query
     * @param variables a JSON-encoded string like <code>{ "myVariable": "someValue", ... }</code>
     * @return the result
     */
    @SuppressWarnings("unchecked")
    @DoGet("/graphql")
    public Object doGet(
        RequestArguments arguments,
        @RequestParam("query") String query,
        @RequestParam("variables") String variables
    ) {
        Map<String, Object> variableMap = Collections.emptyMap();
        if (isNotBlank(variables)) {
            try {
                variableMap = objectMapper.readValue(
                    variables,
                    Map.class
                );
            } catch (Exception e) {
                throw new GraphQLObjectMapperException(
                    singletonMap("variables", "invalid"),
                    e
                );
            }
        }
        return fetch(arguments, query, variableMap);
    }

    /**
     * Follow by this suggestion: <a href="https://graphql.org/learn/serving-over-http/">https://graphql.org/learn/serving-over-http/</a>.
     * Example:
     * <pre>
     * curl --location --request POST 'http://localhost:8083/graphql' \
     *     --header 'Content-Type: application/json' \
     *     --data-raw '{
     *         "query": "{me(id: $id){id+name+friends{name}}}",
     *         "variables": {"id" : 1}
     *      }'
     * </pre>
     *
     * @param arguments the request arguments
     * @param request the request body
     * @return the result
     */
    @DoPost("/graphql")
    public Object doPost(
        RequestArguments arguments,
        @RequestBody GraphQLRequest request
    ) {
        return fetch(
            arguments,
            request.getQuery(),
            request.getVariables()
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked", "MethodLength"})
    private Object fetch(
        RequestArguments arguments,
        String query,
        Map<String, Object> variables
    ) {
        GraphQLSchema schema = schemaParser.parseQuery(query, variables);
        List<GraphQLQueryDefinition> queryDefinitions = schema
            .getQueryDefinitions();
        Map answer = new HashMap<>();

        for (GraphQLQueryDefinition queryDefinition : queryDefinitions) {
            String queryName = queryDefinition.getName();
            GraphQLDataFetcher dataFetcher = dataFetcherManager
                .getDataFetcher(queryName);
            if (dataFetcher == null) {
                throw new HttpNotFoundException(
                    "not found data fetcher with queryName: " + queryName
                );
            }
            List<GraphQLInterceptor> interceptors = interceptorManager
                .getRequestInterceptors();
            String queryGroup = dataFetcherManager.getGroupNameByQueryName(
                queryName
            );
            for (GraphQLInterceptor interceptor : interceptors) {
                boolean ok = interceptor.preHandle(
                    arguments,
                    queryGroup,
                    queryName,
                    queryDefinition,
                    dataFetcher
                );
                if (!ok) {
                    throw new HttpNotAcceptableException(
                        EzyMapBuilder.mapBuilder()
                            .put("controller", "GraphQL")
                            .put("queryGroup", queryGroup)
                            .put("queryName", queryName)
                            .toMap()
                    );
                }
            }
            Object data = dataFetcher.getData(
                arguments,
                queryDefinition
            );
            try {
                Object currentResponse = mapToResponse(
                    data,
                    queryDefinition
                );
                answer.put(queryName, currentResponse);
            } catch (GraphQLObjectMapperException e) {
                answer.put(queryName, data);
            }
            for (GraphQLInterceptor interceptor : interceptors) {
                interceptor.postHandle(
                    arguments,
                    queryGroup,
                    queryName,
                    queryDefinition,
                    answer,
                    dataFetcher
                );
            }
        }
        return answer;
    }

    @SuppressWarnings({"rawtypes"})
    private Map mapToResponse(
        Object data,
        GraphQLQueryDefinition queryDefinition
    ) {
        Map dataMap;
        try {
            dataMap = objectMapper.convertValue(data, Map.class);
        } catch (Exception e) {
            throw new GraphQLObjectMapperException(
                singletonMap("response", "invalid")
            );
        }
        return filterDataMap(dataMap, queryDefinition);
    }

    @SuppressWarnings("rawtypes")
    private Map filterDataMap(
        Map dataMap,
        GraphQLField queryDefinition
    ) {
        return dataFilter.filter(dataMap, queryDefinition);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<GraphQLController> {
        private boolean authenticated;
        private ObjectMapper objectMapper;
        private GraphQLSchemaParser schemaParser;
        private GraphQLDataFilter dataFilter;
        private GraphQLDataFetcherManager dataFetcherManager;
        private GraphQLInterceptorManager interceptorManager;

        public Builder authenticated(boolean authenticated) {
            this.authenticated = authenticated;
            return this;
        }

        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public Builder schemaParser(GraphQLSchemaParser schemaParser) {
            this.schemaParser = schemaParser;
            return this;
        }

        public Builder dataFilter(GraphQLDataFilter dataFilter) {
            this.dataFilter = dataFilter;
            return this;
        }

        public Builder dataFetcherManager(
            GraphQLDataFetcherManager dataFetcherManager
        ) {
            this.dataFetcherManager = dataFetcherManager;
            return this;
        }

        public Builder interceptorManager(
            GraphQLInterceptorManager interceptorManager
        ) {
            this.interceptorManager = interceptorManager;
            return this;
        }

        @Override
        public GraphQLController build() {
            return new GraphQLController(this);
        }
    }
}
