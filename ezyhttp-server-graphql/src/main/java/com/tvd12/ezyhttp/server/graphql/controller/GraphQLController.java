package com.tvd12.ezyhttp.server.graphql.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.DoPost;
import com.tvd12.ezyhttp.server.core.annotation.RequestBody;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;
import com.tvd12.ezyhttp.server.graphql.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.GraphQLDataFetcherManager;
import com.tvd12.ezyhttp.server.graphql.GraphQLField;
import com.tvd12.ezyhttp.server.graphql.GraphQLQueryDefinition;
import com.tvd12.ezyhttp.server.graphql.GraphQLSchema;
import com.tvd12.ezyhttp.server.graphql.GraphQLSchemaParser;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLRequest;

public class GraphQLController {
	
	private final ObjectMapper objectMapper;
	private final GraphQLSchemaParser schemaParser;
	private final GraphQLDataFetcherManager dataFetcherManager;
	
	public GraphQLController(Builder builder) {
		this.objectMapper = builder.objectMapper;
		this.schemaParser = builder.schemaParser;
		this.dataFetcherManager = builder.dataFetcherManager;
	}
	
	/**
	 * Follow by this suggestion: https://graphql.org/learn/serving-over-http/
	 * <p>
	 * Example:
	 *
	 * <code>
	 * curl --location -g --request GET 'http://localhost:8083/graphql?query={me{id+name+friends{name}}}&variables={"id" : 1}'
	 * </code>
	 *
	 * @param query     GraphQL query
	 * @param variables a JSON-encoded string like <code>{ "myVariable": "someValue", ... }</cocde>
	 * @return the result
	 * @throws Exception when have any error
	 */
	@DoGet("/graphql")
	public Object doGet(
			@RequestParam("query") String query,
			@RequestParam("variables") String variables
	) throws Exception {
		return fetch(query, variables);
	}
	
	/**
	 * Follow by this suggestion: https://graphql.org/learn/serving-over-http/
	 * <p>
	 * Example:
	 *
	 * <pre>
	 * curl --location --request POST 'http://localhost:8083/graphql' \
	 * 	--header 'Content-Type: application/json' \
	 * 	--data-raw '{
	 * 		"query": "{me{id+name+friends{name}}}",
	 * 		"variables": {"id" : 1}
	 *      }'
	 * </pre>
	 *
	 * @param request the request body
	 * @return the result
	 * @throws Exception when have any error
	 */
	@DoPost("/graphql")
	public Object doPost(@RequestBody GraphQLRequest request) throws Exception {
		return fetch(
				request.getQuery(),
				request.getVariables()
		);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private Object fetch(
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
				throw new HttpNotFoundException("not found data fetcher with queryName: " + queryName);
			}
			Class<?> argumentType = dataFetcher.getArgumentType();
			Object argument = variables;
			if (argumentType != null) {
				if (variables instanceof String)
					argument = objectMapper.readValue((String) variables, argumentType);
				else
					argument = objectMapper.convertValue(variables, argumentType);
			} else {
				if (variables instanceof String)
					argument = objectMapper.readValue((String) variables, Map.class);
			}
			Object data = dataFetcher.getData(argument);
			try {
				Object currentResponse = mapToResponse(data, queryDefinition, query);
				answer.put(queryName, currentResponse);
			} catch (IllegalArgumentException e) {
				answer.put(queryName, data);
			}
		}
		return answer;
	}
	
	@SuppressWarnings({"rawtypes"})
	private Map mapToResponse(Object data, GraphQLQueryDefinition queryDefinition, String query) {
		Map dataMap = objectMapper.convertValue(data, Map.class);
		return filterDataMap(dataMap, queryDefinition, query);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private Map filterDataMap(Map dataMap, GraphQLField queryDefinition, String query) {
		
		Map answer = new HashMap<>();
		Map parentMap = null;
		Stack<GraphQLField> stack = new Stack<>();
		stack.add(queryDefinition);
		while (stack.size() > 0) {
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
					parentMap.put(field.getName(), filterDataList((List) value, field, query));
					continue;
				} else {
					throw new IllegalStateException("invalid schema: " + query + " at: " + field.getName());
				}
			}
		}
		return answer;
	}
	
	@SuppressWarnings({"rawtypes"})
	private List<Map> filterDataList(List<Map> dataList, GraphQLField queryDefinition, String query) {
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
		private ObjectMapper objectMapper;
		private GraphQLSchemaParser schemaParser;
		private GraphQLDataFetcherManager dataFetcherManager;
		
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
		
		@Override
		public GraphQLController build() {
			return new GraphQLController(this);
		}
	}
}
