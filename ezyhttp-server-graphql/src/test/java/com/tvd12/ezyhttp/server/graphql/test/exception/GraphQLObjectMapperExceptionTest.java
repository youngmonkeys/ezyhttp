package com.tvd12.ezyhttp.server.graphql.test.exception;

import com.tvd12.ezyhttp.server.graphql.data.GraphQLError;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLObjectMapperException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class GraphQLObjectMapperExceptionTest {

    @Test
    public void test() {
        // given
        List<GraphQLError> errors = Collections.singletonList(
            GraphQLError.builder().message("invalid arguments: test").build()
        );
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
