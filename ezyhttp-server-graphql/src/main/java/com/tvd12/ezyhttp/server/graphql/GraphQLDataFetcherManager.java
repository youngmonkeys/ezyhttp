package com.tvd12.ezyhttp.server.graphql;

import java.util.HashMap;
import java.util.Map;

public class GraphQLDataFetcherManager {
	
	private final Map<String, GraphQLDataFetcher> dataFetchers = new HashMap<>();
	
	public void addDataFetcher(String operationName, GraphQLDataFetcher fetcher) {
		this.dataFetchers.put(operationName, fetcher);
	}
	
	public GraphQLDataFetcher getDataFetcher(String operationName) {
		return dataFetchers.get(operationName);
	}
}
