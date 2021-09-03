package com.tvd12.ezyhttp.server.graphql.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.DoPost;
import com.tvd12.ezyhttp.server.core.annotation.RequestBody;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;
import com.tvd12.ezyhttp.server.graphql.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.GraphQLDataFetcherManager;
import com.tvd12.ezyhttp.server.graphql.GraphQLField;
import com.tvd12.ezyhttp.server.graphql.GraphQLRequest;
import com.tvd12.ezyhttp.server.graphql.GraphQLSchema;
import com.tvd12.ezyhttp.server.graphql.GraphQLSchemaParser;

@Controller
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
	 * 
	 * @param operationName be used to control which one should be executed.
	 * @param query GraphQL query
	 * @param variables a JSON-encoded string like <code>{ "myVariable": "someValue", ... }</cocde>
	 * @return the result
	 * @throws Exception when have any error
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@DoGet("/graphql")
	public Object doGet(
			@RequestParam("operationName") String operationName,
			@RequestParam("query") String query,
			@RequestParam("variables") String variables
	) throws Exception {
		GraphQLDataFetcher dataFetcher = dataFetcherManager.getDataFetcher(operationName);
		if(dataFetcher == null) {
			throw new HttpNotFoundException("not found data fetcher with operationName: " + operationName);
		}
		Class<?> argumentType = dataFetcher.getArgumentType();
		Object argument = null;
		if(argumentType != null) {
			argument = objectMapper.readValue(variables, argumentType);
		}
		else {
			argument = objectMapper.readValue(variables, Map.class);
		}
		Object data = dataFetcher.getData(argument);
		return mapToResponse(data, query);
	}
	
	/**
	 * Follow by this suggestion: https://graphql.org/learn/serving-over-http/
	 * 
	 * @param request the request body
	 * @return the result
	 * @throws Exception when have any error
	 */
	@DoPost
	private Object doPost(@RequestBody GraphQLRequest request) throws Exception {
		return doGet(
				request.getOperationName(), 
				request.getQuery(),
				request.getVariables()
		);
	}
	
	@SuppressWarnings({ "rawtypes" })
	private Map mapToResponse(Object data, String query) {
		Map dataMap = objectMapper.convertValue(data, Map.class);
		GraphQLSchema schema = schemaParser.parseQuery(query);
		return filterDataMap(dataMap, schema, query);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map filterDataMap(Map dataMap, GraphQLField schema, String query) {
		Map answer = new HashMap<>();
		Map prentMap = null;
		Stack<GraphQLField> stack = new Stack<>();
		stack.add(schema);
		while(stack.size() > 0) {
			GraphQLField parent = stack.pop();
			prentMap = prentMap == null 
					? answer 
					: (Map) prentMap.get(parent.getName());
			
			for(GraphQLField field : parent.getFields()) {
				Object value = dataMap.get(field.getName());
				if(value == null) {
					continue;
				}
				if(field.getFields().isEmpty()) {
					prentMap.put(field.getName(), value);
					continue;
				}
				if(value instanceof Map) {
					Object newItem = new HashMap<>();
					prentMap.put(field.getName(), newItem);
					stack.push(field);
				}
				else if(value instanceof List) {
					prentMap.put(field.getName(), filterDataList((List)value, field, query));
					continue;
				}
				else {
					throw new IllegalArgumentException("invalid schema: " + query + " at: " + field.getName());
				}
			}
		}
		return answer;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private List<Map> filterDataList(List<Map> dataList, GraphQLField schema, String query) {
		List<Map> answer = new LinkedList<>();
		for(Map map : dataList) {
			answer.add(filterDataMap(map, schema, query));
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
