package com.tvd12.ezyhttp.server.graphql;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.annotation.EzyProperty;
import com.tvd12.ezyfox.bean.EzyBeanConfig;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.bean.EzySingletonFactoryAware;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationAfter;
import com.tvd12.ezyhttp.server.graphql.controller.GraphQLController;

import lombok.Setter;

@Setter
@EzyConfigurationAfter
public class GraphQLConfiguration implements 
		EzyBeanConfig,
		EzySingletonFactoryAware {

	@EzyProperty("graphql.enable")
	private boolean graphQLEnable;
	
	@EzyAutoBind
	private ObjectMapper objectMapper;
	
	private EzySingletonFactory singletonFactory;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void config() {
		GraphQLSchemaParser schemaParser = new GraphQLSchemaParser();
		
		GraphQLDataFetcherManager.Builder dataFetcherManagerBuilder = 
				GraphQLDataFetcherManager.builder();
		List singletons = singletonFactory.getSingletons();
		for(Object singleton : singletons) {
			dataFetcherManagerBuilder.addDataFetcher(singleton);
		}
		GraphQLDataFetcherManager dataFetcherManager = dataFetcherManagerBuilder
				.build();
		GraphQLController controller = GraphQLController.builder()
				.objectMapper(objectMapper)
				.dataFetcherManager(dataFetcherManager)
				.schemaParser(schemaParser)
				.build();
		singletonFactory.addSingleton(controller);
	}
}
