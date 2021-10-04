package com.tvd12.ezyhttp.server.graphql;


import java.util.Stack;

public final class GraphQLSchemaParser {
	
	public GraphQLSchema parseQuery(String queryToParse) {
		String query = standardize(queryToParse);
		
		Stack<GraphQLField.Builder> stack = new Stack<>();
		GraphQLSchema.Builder schemaBuilder = GraphQLSchema.builder();
		
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
				if (nameLength > 0) {
					childBuilder.name(String.copyValueOf(nameBuffer, 0, nameLength));
					nameLength = 0;
				}
				
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
	
	/**
	 * Remove redundant '\t', '\n', '+', ',', ' ' in query
	 *
	 * @param query query in original format
	 * @return standardized query
	 */
	private String standardize(String query) {
		if (query == null) {
			return "";
		}
		String trimedQuery = query.trim();
		StringBuilder forwardStandard = forwardStandardize(trimedQuery);
		StringBuilder backwardStandard = backwardStandardize(forwardStandard.toString());
		return removeQueryPrefix(backwardStandard.toString());
	}
	
	private StringBuilder forwardStandardize(String query) {
		StringBuilder answer = new StringBuilder();
		for (int i = 0; i < query.length(); ++i) {
			char ch = query.charAt(i);
			if (ch == '{' || ch == '}') {
				answer.append(ch);
			} else if (ch == '+' || ch == ',' || ch == ' ' || ch == '\t' || ch == '\n') {
				char lastChar = answer.charAt(answer.length() - 1);
				if ((lastChar != ' ') && (lastChar != '{')) {
					answer.append(' ');
				}
			} else {
				answer.append(ch);
			}
		}
		return answer;
	}
	
	private StringBuilder backwardStandardize(String query) {
		StringBuilder answer = new StringBuilder();
		for (int i = query.length() - 1; i >= 0; --i) {
			char ch = query.charAt(i);
			if (ch == '{' || ch == '}') {
				answer.insert(0, ch);
			} else if (ch == '+' || ch == ',' || ch == ' ' || ch == '\t' || ch == '\n') {
				char firstChar = answer.charAt(0);
				if ((firstChar != ' ') && (firstChar != '{') && (firstChar != '}')) {
					answer.insert(0, ' ');
				}
			} else {
				answer.insert(0, ch);
			}
		}
		return answer;
	}
	
	private String removeQueryPrefix(String s) {
		String prefix = "query";
		if (s.startsWith(prefix)) {
			return s.substring(prefix.length());
		}
		return s;
	}
}
