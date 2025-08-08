package com.tvd12.ezyhttp.server.graphql.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.annotation.EzyProperty;
import com.tvd12.ezyfox.bean.EzyBeanConfig;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.bean.EzySingletonFactoryAware;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationAfter;
import com.tvd12.ezyhttp.server.graphql.controller.GraphQLController;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLDataFilter;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcherManager;
import com.tvd12.ezyhttp.server.graphql.interceptor.GraphQLInterceptorManager;
import com.tvd12.ezyhttp.server.graphql.json.GraphQLObjectMapperFactory;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchemaParser;
import lombok.Setter;

import java.util.List;

@Setter
@EzyConfigurationAfter
public class GraphQLConfiguration implements 
        EzyBeanConfig,
        EzySingletonFactoryAware {

    @EzyProperty("graphql.enable")
    private boolean graphQLEnable;

    @EzyProperty("graphql.authenticated")
    private boolean graphQLAuthenticated;

    private EzySingletonFactory singletonFactory;

    @SuppressWarnings("unchecked")
    @Override
    public void config() {
        if (!graphQLEnable) {
            return;
        }
        GraphQLObjectMapperFactory graphQLObjectMapperFactory =
            new GraphQLObjectMapperFactory();
        ObjectMapper objectMapper = graphQLObjectMapperFactory
            .newObjectMapper();
        GraphQLSchemaParser schemaParser = new GraphQLSchemaParser(
            objectMapper
        );
        GraphQLDataFilter dataFilter = new GraphQLDataFilter();
        GraphQLDataFetcherManager.Builder dataFetcherManagerBuilder =
            GraphQLDataFetcherManager.builder();
        List<GraphQLDataFetcher> dataFetchers = singletonFactory
            .getSingletonsOf(GraphQLDataFetcher.class);
        for (GraphQLDataFetcher dataFetcher : dataFetchers) {
            dataFetcherManagerBuilder.addDataFetcher(dataFetcher);
        }
        GraphQLDataFetcherManager dataFetcherManager = dataFetcherManagerBuilder
            .build();
        GraphQLInterceptorManager interceptorManager =
            new GraphQLInterceptorManager(singletonFactory);
        GraphQLController controller = GraphQLController.builder()
            .authenticated(graphQLAuthenticated)
            .objectMapper(objectMapper)
            .schemaParser(schemaParser)
            .dataFilter(dataFilter)
            .dataFetcherManager(dataFetcherManager)
            .interceptorManager(interceptorManager)
            .build();
        singletonFactory.addSingleton(controller);
    }
}
