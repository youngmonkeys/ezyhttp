package com.tvd12.ezyhttp.server.graphql;

import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfox.builder.EzyBuilder;

@SuppressWarnings("rawtypes")
public class GraphQLDataFetcherManager {
	
	private final Map<String, GraphQLDataFetcher> dataFetchers;
	
	protected GraphQLDataFetcherManager(Builder builder) {
		this.dataFetchers = builder.dataFetchers;
	}
	
	public GraphQLDataFetcher getDataFetcher(String operationName) {
		return dataFetchers.get(operationName);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder implements EzyBuilder<GraphQLDataFetcherManager> {
		
		private final Map<String, GraphQLDataFetcher> dataFetchers = new HashMap<>();
		
		public Builder addDataFetcher(Object fetcher) {
			if(fetcher instanceof GraphQLDataFetcher) {
				GraphQLDataFetcher f = (GraphQLDataFetcher)fetcher;
				return addDataFetcher(f.getOperationName(), f);
			}
			return this;
		}
		
		public Builder addDataFetcher(String operationName, GraphQLDataFetcher fetcher) {
			this.dataFetchers.put(operationName, fetcher);
			return this;
		}
		
		@Override
		public GraphQLDataFetcherManager build() {
			return new GraphQLDataFetcherManager(this);
		}
	}
}
