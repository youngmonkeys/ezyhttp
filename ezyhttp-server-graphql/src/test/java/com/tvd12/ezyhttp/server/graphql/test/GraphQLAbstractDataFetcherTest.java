package com.tvd12.ezyhttp.server.graphql.test;

import com.tvd12.ezyhttp.server.graphql.test.datafetcher.GraphQLNoNameDataFetcher;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

@SuppressWarnings("rawtypes")
public class GraphQLAbstractDataFetcherTest {

    @Test
    public void testExceptionWhenGetArgumentType() {
        // given
        GraphQLNoNameDataFetcher noNameDataFetcher = new GraphQLNoNameDataFetcher();

        // when
        Class c = noNameDataFetcher.getParameterType();

        // then
        Asserts.assertNull(c);
    }
}
