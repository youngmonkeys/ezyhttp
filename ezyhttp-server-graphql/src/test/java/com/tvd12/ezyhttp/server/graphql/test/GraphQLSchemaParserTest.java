package com.tvd12.ezyhttp.server.graphql.test;

import com.tvd12.ezyhttp.server.graphql.GraphQLSchema;
import com.tvd12.ezyhttp.server.graphql.GraphQLSchemaParser;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

public class GraphQLSchemaParserTest {
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testStandardize1() {
		// given
		GraphQLSchemaParser parser = new GraphQLSchemaParser();
		String query = "{}";
		
		// when
		GraphQLSchema schema = parser.parseQuery(query);
		
		// then
		Asserts.assertEquals(schema.getQueryDefinitions().size(), 1);
		String queryName = schema.getQueryDefinitions().get(0).getName();
	}
	
	@Test
	public void testStandardize2() {
		// given
		GraphQLSchemaParser parser = new GraphQLSchemaParser();
		String expectedQueryName = RandomUtil.randomShortAlphabetString();
		String query = "{" + expectedQueryName + "}";
		
		// when
		GraphQLSchema schema = parser.parseQuery(query);
		
		// then
		Asserts.assertEquals(schema.getQueryDefinitions().size(), 1);
		Asserts.assertEquals(schema.getQueryDefinitions().get(0).getName(), expectedQueryName);
	}
	
	@Test
	public void testStandardize3() {
		// given
		GraphQLSchemaParser parser = new GraphQLSchemaParser();
		
		String[] queries = {
				"{queryName}",
				"{ queryName}",
				"{queryName }",
				"{ queryName }",
				"{ \tqueryName\t}",
				"{ \tqueryName\t }",
				"{\t queryName \t}",
				"{ \t  \t   queryName    \t \t \t\t\t}",
				"{\n\tqueryName\n}",
				"{ queryName, }",
				"{ queryName+ }",
				"{ queryName\t }",
		};
		int numQueries = queries.length;
		GraphQLSchema[] schemas = new GraphQLSchema[numQueries];
		
		// when
		for (int i = 0; i < numQueries; ++i) {
			schemas[i] = parser.parseQuery(queries[i]);
		}
		// then
		for (int i = 0; i < numQueries; ++i) {
			Asserts.assertEquals(schemas[i].getQueryDefinitions().size(), 1);
			Asserts.assertEquals(schemas[i].getQueryDefinitions().get(0).getName(), "queryName");
		}
	}
	
	@Test
	public void testStandardize4() {
		// given
		GraphQLSchemaParser parser = new GraphQLSchemaParser();
		
		String[] queries = {
				"{queryName{field1 field2}}",
				"{queryName{field1+field2}}",
				"{queryName{field1,field2}}",
				" { queryName { field1 field2 } } ",
				" {\n\tqueryName {\n\t\tfield1\n\t\tfield2\n\t}\n}",
				"{ queryName  \t{field1\t \n \t + \n \t field2}  \t\n\t}",
				"query { queryName  \t{field1\t \n \t + \n \t field2}  \t\n\t}",
		};
		int numQueries = queries.length;
		GraphQLSchema[] schemas = new GraphQLSchema[numQueries];
		
		// when
		for (int i = 0; i < numQueries; ++i) {
			schemas[i] = parser.parseQuery(queries[i]);
		}
		// then
		for (int i = 0; i < numQueries; ++i) {
			Asserts.assertEquals(schemas[i].getQueryDefinitions().size(), 1);
			Asserts.assertEquals(schemas[i].getQueryDefinitions().get(0).getName(), "queryName");
			Asserts.assertEquals(schemas[i].getQueryDefinitions().get(0).getFields().size(), 2);
		}
	}
}
