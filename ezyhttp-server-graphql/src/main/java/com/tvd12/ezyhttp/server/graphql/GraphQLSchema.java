package com.tvd12.ezyhttp.server.graphql;

public class GraphQLSchema extends GraphQLField {
	
	protected GraphQLSchema(Builder builder) {
		super(builder);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends GraphQLField.Builder {
		
		@Override
		public Builder name(String name) {
			return (Builder)super.name(name);
		}
		
		@Override
		public Builder addField(GraphQLField field) {
			return (Builder)super.addField(field);
		}
		
		@Override
		public GraphQLSchema build() {
			return new GraphQLSchema(this);
		}
	}
}
