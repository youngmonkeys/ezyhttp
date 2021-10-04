package com.tvd12.ezyhttp.server.graphql.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.exception.EzyNotImplementedException;
import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import com.tvd12.ezyhttp.server.graphql.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.GraphQLDataFetcherManager;
import com.tvd12.ezyhttp.server.graphql.GraphQLSchemaParser;
import com.tvd12.ezyhttp.server.graphql.controller.GraphQLController;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLRequest;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLInvalidSchemeException;
import com.tvd12.ezyhttp.server.graphql.test.datafetcher.*;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class GraphQLControllerTest {
	
	@Test
	public void test() throws Exception {
		// given
		GraphQLSchemaParser schemaParser = new GraphQLSchemaParser();
		GraphQLDataFetcher meDataFetcher = new GraphQLMeDataFetcher();
		GraphQLDataFetcher heroDataFetcher = new GraphQLHeroDataFetcher();
		GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder()
				.addDataFetcher(meDataFetcher)
				.addDataFetcher(heroDataFetcher).build();
		ObjectMapper objectMapper = new ObjectMapper();
		
		GraphQLController controller = GraphQLController.builder()
				.schemaParser(schemaParser).dataFetcherManager(dataFetcherManager).objectMapper(objectMapper).build();
		
		GraphQLRequest meRequest = new GraphQLRequest();
		meRequest.setQuery("query{    me   {     name bank{id} friends{name} address}}");
		
		String heroQuery = "{hero}";
		
		// when
		Object meResult = controller.doPost(meRequest);
		Object heroResult = controller.doGet(heroQuery, null);
		
		// then
		Asserts.assertEquals(meResult.toString(), "{me={bank={id=1}, name=Dzung, friends=[{name=Foo}, {name=Bar}]}}");
		Asserts.assertEquals(heroResult.toString(), "{hero=Hero 007}");
	}
	
	@Test
	public void testFetcherNotFoundException() throws Exception {
		// given
		GraphQLSchemaParser schemaParser = new GraphQLSchemaParser();
		GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder().build();
		ObjectMapper objectMapper = new ObjectMapper();
		
		GraphQLController controller = GraphQLController.builder()
				.schemaParser(schemaParser).dataFetcherManager(dataFetcherManager).objectMapper(objectMapper).build();
		
		String heroQuery = "{hero}";
		
		// when
		Throwable e = Asserts.assertThrows(() -> controller.doGet(heroQuery, null));
		
		// then
		Asserts.assertEquals(HttpNotFoundException.class.toString(), e.getClass().toString());
	}
	
	@Test
	public void testQueryWithVariables() throws Exception {
		// given
		GraphQLSchemaParser schemaParser = new GraphQLSchemaParser();
		GraphQLDataFetcher welcomeDataFetcher = new GraphQLWelcomeDataFetcher();
		GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder()
				.addDataFetcher(welcomeDataFetcher).build();
		ObjectMapper objectMapper = new ObjectMapper();
		
		GraphQLController controller = GraphQLController.builder()
				.schemaParser(schemaParser).dataFetcherManager(dataFetcherManager).objectMapper(objectMapper).build();
		
		String welcomeQuery = "{welcome}";
		String variables = "{\"name\": \"Foo\"}";
		GraphQLRequest welcomeRequest = new GraphQLRequest();
		welcomeRequest.setQuery(welcomeQuery);
		welcomeRequest.setVariables(variables);
		
		// when
		Object welcomeResult1 = controller.doGet(welcomeQuery, variables);
		Object welcomeResult2 = controller.doPost(welcomeRequest);
		
		// then
		Asserts.assertEquals(welcomeResult1.toString(), "{welcome=Welcome Foo}");
		Asserts.assertEquals(welcomeResult2.toString(), "{welcome=Welcome Foo}");
	}
	
	@Test
	public void testQueryWithNullVariableType() throws Exception {
		// given
		GraphQLSchemaParser schemaParser = new GraphQLSchemaParser();
		GraphQLDataFetcher fooDataFetcher = new GraphQLFooDataFetcher();
		GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder()
				.addDataFetcher(fooDataFetcher).build();
		ObjectMapper objectMapper = new ObjectMapper();
		
		GraphQLController controller = GraphQLController.builder()
				.schemaParser(schemaParser).dataFetcherManager(dataFetcherManager).objectMapper(objectMapper).build();
		
		String fooQuery = "{foo}";
		
		// when
		Object fooResult1 = controller.doGet(fooQuery, "{\"value\": \"Bar\"}");
		Object fooResult2 = controller.doGet(fooQuery, null);
		
		// then
		Asserts.assertEquals(fooResult1.toString(), "{foo=Foo {value=Bar}}");
		Asserts.assertEquals(fooResult2.toString(), "{foo=Foo null}");
	}
	
	@Test
	public void testInvalidScheme() {
		// given
		GraphQLSchemaParser schemaParser = new GraphQLSchemaParser();
		GraphQLDataFetcher meDataFetcher = new GraphQLYouDataFetcher();
		GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder()
				.addDataFetcher(meDataFetcher)
				.build();
		ObjectMapper objectMapper = new ObjectMapper();
		
		GraphQLController controller = GraphQLController.builder()
				.schemaParser(schemaParser).dataFetcherManager(dataFetcherManager).objectMapper(objectMapper).build();
		
		GraphQLRequest youRequest = new GraphQLRequest();
		youRequest.setQuery("query{you{friends{name}}}}");
		
		// when
		Throwable e = Asserts.assertThrows(() -> controller.doPost(youRequest));
		
		// then
		Asserts.assertEquals(GraphQLInvalidSchemeException.class.toString(), e.getClass().toString());
	}
	
	@Test
	public void testNoNameDataFetcher() {
		// given
		GraphQLDataFetcher nonameDataFetcher = new GraphQLNoNameDataFetcher();
		
		// when
		Throwable e = Asserts.assertThrows(() -> GraphQLDataFetcherManager.builder().addDataFetcher(nonameDataFetcher).build());
		
		// then
		Asserts.assertEquals(EzyNotImplementedException.class.toString(), e.getClass().toString());
	}
	
}
