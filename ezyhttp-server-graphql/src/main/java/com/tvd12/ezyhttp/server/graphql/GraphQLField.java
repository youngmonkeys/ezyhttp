package com.tvd12.ezyhttp.server.graphql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tvd12.ezyfox.builder.EzyBuilder;

import lombok.Getter;

@Getter
public class GraphQLField {
	
	protected final String name;
	protected final List<GraphQLField> fields;
	
	protected GraphQLField(Builder builder) {
		this.name = builder.name;
		this.fields = builder.fields != null ? builder.fields : Collections.emptyList(); 
	}
	
	@Override
	public String toString() {
		return name + ", " + fields;
	}
	
	@Override
	public boolean equals(Object obj) {
		return name.equals(((GraphQLField)obj).name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder implements EzyBuilder<GraphQLField> {
		private String name;
		private List<GraphQLField> fields;
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder addField(GraphQLField field) {
			if(fields == null)
				fields = new ArrayList<>();
			this.fields.add(field);
			return this;
		}
		
		@Override
		public GraphQLField build() {
			return new GraphQLField(this);
		}
	}
}
