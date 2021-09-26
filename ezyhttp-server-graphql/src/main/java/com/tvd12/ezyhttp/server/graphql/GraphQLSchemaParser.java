package com.tvd12.ezyhttp.server.graphql;

import java.util.Stack;

public final class GraphQLSchemaParser {
	
	public GraphQLSchema parseQuery(String query) {
		// TODO: standardize query (remove redundant space, tab...)
		Stack<GraphQLField.Builder> stack = new Stack<>();
		GraphQLSchema.Builder schemaBuilder = new GraphQLSchema.Builder();
		
		int nameLength = 0;
		char[] nameBuffer = new char[128];
		for (int i = 0; i < query.length(); ++i) {
			char ch = query.charAt(i);
			if (ch == '{') {
				if (stack.isEmpty()) {
					GraphQLQueryDefinition.Builder queryBuilder = GraphQLQueryDefinition.builder();
					stack.add(queryBuilder);
					continue;
				}
				
				GraphQLField.Builder builder = stack.peek();
				if (nameLength > 0) {
					builder.name(String.copyValueOf(nameBuffer, 0, nameLength));
					nameLength = 0;
				}
				GraphQLField.Builder childBuilder = GraphQLField.builder();
				stack.add(childBuilder);
				continue;
			}
			
			if (ch == '}') {
				if (stack.isEmpty()) {
					continue;
				}
				if (stack.size() == 1) {
					GraphQLField.Builder item = stack.pop();
					if (nameLength > 0) {
						item.name(String.copyValueOf(nameBuffer, 0, nameLength));
						nameLength = 0;
					}
					schemaBuilder.addQueryDefinition((GraphQLQueryDefinition) item.build());
					continue;
				}
				
				GraphQLField.Builder childBuilder = stack.pop();
				if (nameLength > 0) {
					childBuilder.name(String.copyValueOf(nameBuffer, 0, nameLength));
					nameLength = 0;
				}
				
				GraphQLField.Builder parentBuilder = stack.peek();
				parentBuilder.addField(childBuilder.build());
				
				if (stack.size() == 1) {
					GraphQLField.Builder item = stack.pop();
					schemaBuilder.addQueryDefinition((GraphQLQueryDefinition) item.build());
				}
			} else if (ch == '+' || ch == ',' || ch == ' ' || ch == '\t' || ch == '\n') {
				if (stack.isEmpty()) {
					GraphQLQueryDefinition.Builder queryBuilder = GraphQLQueryDefinition.builder();
					stack.add(queryBuilder);
					nameLength = 0;
					continue;
				}
				
				if (stack.size() == 1) {
					GraphQLField.Builder item = stack.pop();
					item.name(String.copyValueOf(nameBuffer, 0, nameLength));
					nameLength = 0;
					schemaBuilder.addQueryDefinition((GraphQLQueryDefinition) item.build());
					
					GraphQLQueryDefinition.Builder queryBuilder = GraphQLQueryDefinition.builder();
					stack.add(queryBuilder);
					continue;
				}
				
				GraphQLField.Builder childBuilder = stack.pop();
				childBuilder.name(String.copyValueOf(nameBuffer, 0, nameLength));
				nameLength = 0;
				
				GraphQLField.Builder parentBuilder = stack.peek();
				parentBuilder.addField(childBuilder.build());
				
				GraphQLField.Builder newChildBuilder = GraphQLField.builder();
				stack.add(newChildBuilder);
			} else {
				nameBuffer[nameLength++] = ch;
			}
		}
		return schemaBuilder.build();
	}
}
