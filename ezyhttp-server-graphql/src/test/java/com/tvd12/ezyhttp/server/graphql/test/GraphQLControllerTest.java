package com.tvd12.ezyhttp.server.graphql.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import com.tvd12.ezyhttp.server.graphql.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.GraphQLDataFetcherManager;
import com.tvd12.ezyhttp.server.graphql.GraphQLSchemaParser;
import com.tvd12.ezyhttp.server.graphql.controller.GraphQLController;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLRequest;
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
		meRequest.setQuery("query{    me   {     name bank{id} friends{name}}}");
		
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
		Asserts.assertEquals(HttpNotFoundException.class, e.getClass());
	}
}
