package com.tvd12.ezyhttp.server.graphql;

import java.util.List;

import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.OperationDefinition;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import graphql.parser.Parser;

public final class GraphQLProxy {

	private final Parser parser = new Parser();
	private static final GraphQLProxy INSTANCE = new GraphQLProxy();
	
	private GraphQLProxy() {}
	
	public static GraphQLProxy getInstance() {
		return INSTANCE;
	}
	
	public GraphQLSchema parseQuery(String query) {
		Document document = parser.parseDocument(query);
		List<Definition> definitions = document.getDefinitions();
		if(definitions.isEmpty())
			throw new IllegalArgumentException("invalid graphQL query (1): " + query);
		Definition definition = definitions.get(0);
		if(!(definition instanceof OperationDefinition))
			throw new IllegalArgumentException("invalid graphQL query (2): " + query);
		OperationDefinition operationDefinition = (OperationDefinition)definition;
		SelectionSet selectionSet = operationDefinition.getSelectionSet();
		List<Selection> selections = selectionSet.getSelections();
		if(selections.isEmpty())
			throw new IllegalArgumentException("invalid graphQL query (2): " + query);
		Selection<Field> selection = selections.get(0);
		Field field = (Field)selection;
		GraphQLSchema.Builder builder = GraphQLSchema.builder()
				.name(field.getName());
		SelectionSet fieldSelectionSet = field.getSelectionSet();
		if(fieldSelectionSet != null) {
			for(Selection s : fieldSelectionSet.getSelections()) {
				builder.addField(parseField((Field)s));
			}
		}
		return builder.build();
	}
	
	private GraphQLField parseField(Field field) {
		GraphQLField.Builder builder = GraphQLField.builder()
				.name(field.getName());
		SelectionSet fieldSelectionSet = field.getSelectionSet();
		if(fieldSelectionSet != null) {
			for(Selection s : fieldSelectionSet.getSelections()) {
				builder.addField(parseField((Field)s));
			}
		}
		return builder.build();
	}
}
