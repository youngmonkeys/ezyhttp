package com.tvd12.ezyhttp.server.graphql;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GraphQLSchema {
	
	@Getter
	private List<GraphQLQueryDefinition> queryDefinitions;
	
	public GraphQLSchema(Builder builder) {
		this.queryDefinitions = builder.queryDefinitions != null ? builder.queryDefinitions : Collections.emptyList();
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder implements EzyBuilder<GraphQLSchema> {
		private List<GraphQLQueryDefinition> queryDefinitions;
		
		public Builder addQueryDefinition(GraphQLQueryDefinition queryDefinition) {
			if (queryDefinitions == null)
				queryDefinitions = new LinkedList<>();
			this.queryDefinitions.add(queryDefinition);
			return this;
		}
		
		@Override
		public GraphQLSchema build() {
			return new GraphQLSchema(this);
		}
	}
	
}
