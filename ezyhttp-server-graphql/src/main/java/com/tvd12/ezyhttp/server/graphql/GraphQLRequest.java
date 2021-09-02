package com.tvd12.ezyhttp.server.graphql;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tvd12.ezyfox.builder.EzyBuilder;

import lombok.Getter;

@Getter
public class GraphQLRequest {

	private final String name;
	private final List<GraphQLField> fields;
	private Map<String, Object> arguments;
	
	protected GraphQLRequest(Builder builder) {
		this.name = builder.name;
		this.fields = builder.fields; 
	}
	
	@Override
	public String toString() {
		return name + ", " + fields;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder implements EzyBuilder<GraphQLRequest> {
		private String name;
		private final List<GraphQLField> fields = new LinkedList<>();
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder addField(GraphQLField field) {
			this.fields.add(field);
			return this;
		}
		
		@Override
		public GraphQLRequest build() {
			return new GraphQLRequest(this);
		}
	}
}
