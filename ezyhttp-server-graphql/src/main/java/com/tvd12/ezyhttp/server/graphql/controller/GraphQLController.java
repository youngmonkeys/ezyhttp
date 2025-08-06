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
import com.tvd12.ezyhttp.server.graphql.data.GraphQLField;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLRequest;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLInvalidSchemeException;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLObjectMapperException;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcherManager;
import com.tvd12.ezyhttp.server.graphql.interceptor.GraphQLInterceptor;
import com.tvd12.ezyhttp.server.graphql.interceptor.GraphQLInterceptorManager;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchema;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchemaParser;
import lombok.Getter;

import java.util.*;

@Api
@Authenticatable
public class GraphQLController
    implements IRequestController, AuthenticatedController {

    @Getter
    private final boolean authenticated;
    private final ObjectMapper objectMapper;
    private final GraphQLSchemaParser schemaParser;
    private final GraphQLDataFetcherManager dataFetcherManager;
    private final GraphQLInterceptorManager interceptorManager;

    public GraphQLController(Builder builder) {
        this.authenticated = builder.authenticated;
        this.objectMapper = builder.objectMapper;
        this.schemaParser = builder.schemaParser;
        this.dataFetcherManager = builder.dataFetcherManager;
        this.interceptorManager = builder.interceptorManager;
    }

    /**
     * Follow by this suggestion: <a href="https://graphql.org/learn/serving-over-http/">https://graphql.org/learn/serving-over-http/</a>.
     * Example:
     * <code>
     * curl --location -g --request GET 'http://localhost:8083/graphql?query={me{id+name+friends{name}}}&amp;variables={"id" : 1}'
     * </code>
     *
     * @param query     GraphQL query
     * @param variables a JSON-encoded string like <code>{ "myVariable": "someValue", ... }</code>
     * @return the result
     * @throws Exception when have any error
     */
    @DoGet("/graphql")
    public Object doGet(
        RequestArguments arguments,
        @RequestParam("query") String query,
        @RequestParam("variables") String variables
    ) throws Exception {
        return fetch(arguments, query, variables);
    }

    /**
     * Follow by this suggestion: <a href="https://graphql.org/learn/serving-over-http/">https://graphql.org/learn/serving-over-http/</a>.
     * Example:
     * <pre>
     * curl --location --request POST 'http://localhost:8083/graphql' \
     *     --header 'Content-Type: application/json' \
     *     --data-raw '{
     *         "query": "{me{id+name+friends{name}}}",
     *         "variables": {"id" : 1}
     *      }'
     * </pre>
     *
     * @param request the request body
     * @return the result
     * @throws Exception when have any error
     */
    @DoPost("/graphql")
    public Object doPost(
        RequestArguments arguments,
        @RequestBody GraphQLRequest request
    ) throws Exception {
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
        Object variables
    ) throws Exception {
        GraphQLSchema schema = schemaParser.parseQuery(query);
        List<GraphQLQueryDefinition> queryDefinitions = schema.getQueryDefinitions();
        Map answer = new HashMap<>();

        for (GraphQLQueryDefinition queryDefinition : queryDefinitions) {
            String queryName = queryDefinition.getName();
            GraphQLDataFetcher dataFetcher = dataFetcherManager.getDataFetcher(queryName);
            if (dataFetcher == null) {
                throw new HttpNotFoundException(
                    "not found data fetcher with queryName: " + queryName
                );
            }
            Class<?> parameterType = dataFetcher.getParameterType();
            Object parameter = variables;
            if (parameterType != null) {
                if (variables instanceof String) {
                    parameter = objectMapper.readValue((String) variables, parameterType);
                } else {
                    parameter = objectMapper.convertValue(variables, parameterType);
                }
            } else {
                if (variables instanceof String) {
                    parameter = objectMapper.readValue((String) variables, Map.class);
                }
            }
            List<GraphQLInterceptor> interceptors = interceptorManager
                .getRequestInterceptors();
            String queryGroup = dataFetcherManager.getGroupNameByQueryName(queryName);
            for (GraphQLInterceptor interceptor : interceptors) {
                boolean ok = interceptor.preHandle(
                    arguments,
                    queryGroup,
                    queryName,
                    parameter,
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
                parameter
            );
            try {
                Object currentResponse = mapToResponse(data, queryDefinition, query);
                answer.put(queryName, currentResponse);
            } catch (GraphQLObjectMapperException e) {
                answer.put(queryName, data);
            }
            for (GraphQLInterceptor interceptor : interceptors) {
                interceptor.postHandle(
                    arguments,
                    queryGroup,
                    queryName,
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
        GraphQLQueryDefinition queryDefinition,
        String query
    ) {
        Map dataMap;
        try {
            dataMap = objectMapper.convertValue(data, Map.class);
        } catch (Exception e) {
            throw new GraphQLObjectMapperException(
                "Could not convert: " + data.getClass() + " to Map"
            );
        }
        return filterDataMap(dataMap, queryDefinition, query);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Map filterDataMap(
        Map dataMap,
        GraphQLField queryDefinition,
        String query
    ) {
        Map answer = new HashMap<>();
        Map parentMap = null;
        Stack<GraphQLField> stack = new Stack<>();
        stack.add(queryDefinition);
        while (!stack.isEmpty()) {
            GraphQLField parent = stack.pop();
            parentMap = parentMap == null
                ? answer
                : (Map) parentMap.get(parent.getName());

            for (GraphQLField field : parent.getFields()) {
                Object value = dataMap.get(field.getName());
                if (value == null) {
                    continue;
                }
                if (field.getFields().isEmpty()) {
                    parentMap.put(field.getName(), value);
                    continue;
                }
                if (value instanceof Map) {
                    Object newItem = new HashMap<>();
                    parentMap.put(field.getName(), newItem);
                    stack.push(field);
                } else if (value instanceof List) {
                    parentMap.put(
                        field.getName(),
                        filterDataList((List) value, field, query)
                    );
                } else {
                    throw new GraphQLInvalidSchemeException(
                        "invalid schema: " + query + " at: " + field.getName()
                    );
                }
            }
        }
        return answer;
    }

    @SuppressWarnings({"rawtypes"})
    private List<Map> filterDataList(
        List<Map> dataList,
        GraphQLField queryDefinition, String query
    ) {
        List<Map> answer = new LinkedList<>();
        for (Map map : dataList) {
            answer.add(filterDataMap(map, queryDefinition, query));
        }
        return answer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<GraphQLController> {
        private boolean authenticated;
        private ObjectMapper objectMapper;
        private GraphQLSchemaParser schemaParser;
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

        public Builder dataFetcherManager(GraphQLDataFetcherManager dataFetcherManager) {
            this.dataFetcherManager = dataFetcherManager;
            return this;
        }

        public Builder interceptorManager(GraphQLInterceptorManager interceptorManager) {
            this.interceptorManager = interceptorManager;
            return this;
        }

        @Override
        public GraphQLController build() {
            return new GraphQLController(this);
        }
    }
}
