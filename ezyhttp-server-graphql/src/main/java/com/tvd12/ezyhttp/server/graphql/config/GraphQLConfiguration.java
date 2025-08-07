package com.tvd12.ezyhttp.server.graphql.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.annotation.EzyProperty;
import com.tvd12.ezyfox.bean.EzyBeanConfig;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.bean.EzySingletonFactoryAware;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationAfter;
import com.tvd12.ezyhttp.core.json.ObjectMapperBuilder;
import com.tvd12.ezyhttp.server.graphql.controller.GraphQLController;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcherManager;
import com.tvd12.ezyhttp.server.graphql.interceptor.GraphQLInterceptorManager;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchemaParser;
import lombok.Setter;

import java.util.List;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;

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

    @SuppressWarnings("rawtypes")
    @Override
    public void config() {
        if (!graphQLEnable) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapperBuilder().build();
        objectMapper.configure(ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(ALLOW_SINGLE_QUOTES, true);
        GraphQLSchemaParser schemaParser = new GraphQLSchemaParser(
            objectMapper
        );
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
