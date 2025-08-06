package com.tvd12.ezyhttp.server.graphql.config;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.annotation.EzyProperty;
import com.tvd12.ezyfox.bean.EzyBeanConfig;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.bean.EzySingletonFactoryAware;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationAfter;
import com.tvd12.ezyhttp.server.graphql.controller.GraphQLController;

import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcherManager;
import com.tvd12.ezyhttp.server.graphql.interceptor.GraphQLInterceptorManager;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchemaParser;
import lombok.Setter;

@Setter
@EzyConfigurationAfter
public class GraphQLConfiguration implements 
        EzyBeanConfig,
        EzySingletonFactoryAware {

    @EzyProperty("graphql.enable")
    private boolean graphQLEnable;

    @EzyProperty("graphql.authenticated")
    private boolean graphQLAuthenticated;

    @EzyAutoBind
    private ObjectMapper objectMapper;

    private EzySingletonFactory singletonFactory;

    @SuppressWarnings("rawtypes")
    @Override
    public void config() {
        if (!graphQLEnable) {
            return;
        }
        GraphQLSchemaParser schemaParser = new GraphQLSchemaParser();
        GraphQLDataFetcherManager.Builder dataFetcherManagerBuilder =
            GraphQLDataFetcherManager.builder();
        List singletons = singletonFactory.getSingletons();
        for (Object singleton : singletons) {
            dataFetcherManagerBuilder.addDataFetcher(singleton);
        }
        GraphQLDataFetcherManager dataFetcherManager = dataFetcherManagerBuilder
            .build();
        GraphQLInterceptorManager interceptorManager =
            new GraphQLInterceptorManager(singletonFactory);
        GraphQLController controller = GraphQLController.builder()
            .authenticated(graphQLAuthenticated)
            .objectMapper(objectMapper)
            .schemaParser(schemaParser)
            .dataFetcherManager(dataFetcherManager)
            .interceptorManager(interceptorManager)
            .build();
        singletonFactory.addSingleton(controller);
    }
}
