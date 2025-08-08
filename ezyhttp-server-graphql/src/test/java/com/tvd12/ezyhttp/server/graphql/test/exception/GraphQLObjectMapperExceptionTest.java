package com.tvd12.ezyhttp.server.graphql.test.exception;

import com.tvd12.ezyhttp.server.graphql.exception.GraphQLObjectMapperException;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

public class GraphQLObjectMapperExceptionTest {

    @Test
    public void test() {
        // given
        String errors = RandomUtil.randomShortAlphabetString();
        Exception cause = new Exception("test");

        //
        GraphQLObjectMapperException instance = new GraphQLObjectMapperException(
            errors,
            cause
        );

        // then
        Asserts.assertEquals(instance.getErrors(), errors);
        Asserts.assertEquals(instance.getCause(), cause);
    }
}
