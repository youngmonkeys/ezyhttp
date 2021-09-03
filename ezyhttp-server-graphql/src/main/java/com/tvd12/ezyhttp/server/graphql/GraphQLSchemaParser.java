package com.tvd12.ezyhttp.server.graphql;

import java.util.Stack;

public final class GraphQLSchemaParser {

	public GraphQLSchema parseQuery(String query) {
		Stack<GraphQLField.Builder> stack = new Stack<>();
		GraphQLSchema.Builder schemaBuilder = GraphQLSchema.builder();
		stack.add(schemaBuilder);
		int nameLength = 0;
		char[] nameBuffer = new char[128];
		boolean first = true;
		for(int i = 0 ; i < query.length() ; ++i) {
			char ch = query.charAt(i);
			if(ch == '{') {
				if(first) {
					first = false;
					continue;
				}
				GraphQLField.Builder builder = stack.peek();
				if(nameLength > 0) {
					builder.name(String.copyValueOf(nameBuffer, 0, nameLength));
					nameLength = 0;
				}
				GraphQLField.Builder childBuilder = GraphQLField.builder();
				stack.add(childBuilder);
			}
			else if(ch == '}') {
				GraphQLField.Builder childBuilder = stack.pop();
				if(nameLength > 0) {
					childBuilder.name(String.copyValueOf(nameBuffer, 0, nameLength));
					nameLength = 0;
				}
				if(stack.size() > 0) {
					GraphQLField.Builder parentBuilder = stack.peek();
					parentBuilder.addField(childBuilder.build());
				}
			}
			else if(ch == '+' || ch == ',' || ch == ' ' || ch == '\t' || ch == '\n') {
				GraphQLField.Builder childBuilder = stack.pop();
				childBuilder.name(String.copyValueOf(nameBuffer, 0, nameLength));
				nameLength = 0;
				GraphQLField.Builder parentBuilder = stack.peek();
				parentBuilder.addField(childBuilder.build());
				
				GraphQLField.Builder newChildBuilder = GraphQLField.builder();
				stack.add(newChildBuilder);
			}
			else {
				nameBuffer[nameLength ++] = ch;
			}
		}
		return schemaBuilder.build();
	}
}
