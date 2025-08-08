package com.tvd12.ezyhttp.server.graphql.test.datafetcher;

import com.tvd12.ezyfox.exception.EzyNotImplementedException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class GraphQLAbstractDataFetcherTest {

    @Test
    public void testExceptionWhenGetArgumentType() {
        // given
        GraphQLNoNameDataFetcher noNameDataFetcher = new GraphQLNoNameDataFetcher();

        // when
        Throwable e = Asserts.assertThrows(() ->
            System.out.println(noNameDataFetcher.getQueryName())
        );

        // then
        Asserts.assertEqualsType(e, EzyNotImplementedException.class);
    }
}
