package com.tvd12.ezyhttp.server.graphql.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.bean.EzyBeanContextBuilder;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.bean.impl.EzySimpleBeanContext;
import com.tvd12.ezyhttp.server.graphql.GraphQLConfiguration;
import com.tvd12.ezyhttp.server.graphql.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.GraphQLDataFetcherManager;
import com.tvd12.ezyhttp.server.graphql.controller.GraphQLController;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class GraphQLConfigurationTest {

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void test() throws NoSuchFieldException, IllegalAccessException {
        // given
        EzyBeanContextBuilder builder = new EzySimpleBeanContext.Builder();

        Set<String> packagesToScan = RandomUtil.randomSet(8, String.class);
        packagesToScan.add("com.tvd12.ezyhttp.server.graphql.test.config");

        for (String p : packagesToScan) {
            builder.scan(p);
        }

        EzyBeanContext context = builder.build();

        GraphQLConfiguration sut = new GraphQLConfiguration();
        EzySingletonFactory singletonFactory = context.getSingletonFactory();
        sut.setSingletonFactory(singletonFactory);
        sut.setObjectMapper(new ObjectMapper());
        sut.setGraphQLEnable(true);

        // when
        sut.config();
        GraphQLController controller = (GraphQLController) singletonFactory.getSingleton(GraphQLController.class);
        Field dataFetcherManagerField = GraphQLController.class.getDeclaredField("dataFetcherManager");
        dataFetcherManagerField.setAccessible(true);
        GraphQLDataFetcherManager dataFetcherManager = (GraphQLDataFetcherManager) dataFetcherManagerField.get(controller);

        Field dataFetchersField = GraphQLDataFetcherManager.class.getDeclaredField("dataFetchers");
        dataFetchersField.setAccessible(true);
        Map<String, GraphQLDataFetcher> dataFetchers = (Map<String, GraphQLDataFetcher>) dataFetchersField.get(dataFetcherManager);

        // then
        Asserts.assertNotNull(controller);
        Asserts.assertTrue(dataFetchers.containsKey("A"));
    }

}
