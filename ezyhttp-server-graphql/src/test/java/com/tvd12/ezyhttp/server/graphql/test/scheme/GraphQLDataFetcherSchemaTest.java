package com.tvd12.ezyhttp.server.graphql.test.scheme;

import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLDataFetcherSchema;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLDataSchema;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class GraphQLDataFetcherSchemaTest {

    @Test
    public void test() {
        // given
        List<GraphQLDataSchema> queryScheme = Collections.singletonList(
            GraphQLDataSchema.builder()
                .name(RandomUtil.randomShortAlphabetString())
                .type(RandomUtil.randomShortAlphabetString())
                .build()
        );

        GraphQLDataSchema getResponseScheme = GraphQLDataSchema.builder()
            .name(RandomUtil.randomShortAlphabetString())
            .type(RandomUtil.randomShortAlphabetString())
            .build();

        // when
        GraphQLDataFetcherSchema instance = GraphQLDataFetcherSchema.builder()
            .queryScheme(queryScheme)
            .getResponseScheme(getResponseScheme)
            .build();

        // then
        Asserts.assertEquals(instance.getQueryScheme(), queryScheme);
        Asserts.assertEquals(instance.getGetResponseScheme(), getResponseScheme);

        System.out.println(instance);
    }
}
