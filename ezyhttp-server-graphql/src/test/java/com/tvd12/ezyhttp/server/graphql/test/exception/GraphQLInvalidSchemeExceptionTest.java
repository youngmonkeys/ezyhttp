package com.tvd12.ezyhttp.server.graphql.test.exception;

import com.tvd12.ezyhttp.server.graphql.exception.GraphQLInvalidSchemeException;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

public class GraphQLInvalidSchemeExceptionTest {

    @Test
    public void test() {
        // given
        String errors = RandomUtil.randomShortAlphabetString();

        //
        GraphQLInvalidSchemeException instance = new GraphQLInvalidSchemeException(
            errors
        );

        // then
        Asserts.assertEquals(instance.getErrors(), errors);
    }
}
