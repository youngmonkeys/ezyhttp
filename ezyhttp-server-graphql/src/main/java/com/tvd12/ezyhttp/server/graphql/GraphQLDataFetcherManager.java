package com.tvd12.ezyhttp.server.graphql;

import com.tvd12.ezyfox.builder.EzyBuilder;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class GraphQLDataFetcherManager {
	
	private final Map<String, GraphQLDataFetcher> dataFetchers;
	
	protected GraphQLDataFetcherManager(Builder builder) {
		this.dataFetchers = builder.dataFetchers;
	}
	
	public GraphQLDataFetcher getDataFetcher(String queryName) {
		return dataFetchers.get(queryName);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder implements EzyBuilder<GraphQLDataFetcherManager> {
		
		private final Map<String, GraphQLDataFetcher> dataFetchers = new HashMap<>();
		
		public Builder addDataFetcher(Object fetcher) {
			if(fetcher instanceof GraphQLDataFetcher) {
				GraphQLDataFetcher f = (GraphQLDataFetcher)fetcher;
				return addDataFetcher(f.getQueryName(), f);
			}
			return this;
		}
		
		public Builder addDataFetcher(String queryName, GraphQLDataFetcher fetcher) {
			this.dataFetchers.put(queryName, fetcher);
			return this;
		}
		
		@Override
		public GraphQLDataFetcherManager build() {
			return new GraphQLDataFetcherManager(this);
		}
	}
}
