package com.tvd12.ezyhttp.server.graphql.test.exception;

import com.tvd12.ezyhttp.server.graphql.data.GraphQLError;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLInvalidSchemeException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class GraphQLInvalidSchemeExceptionTest {

    @Test
    public void test() {
        // given
        List<GraphQLError> errors = Collections.singletonList(
            GraphQLError.builder().message("invalid schema for field: foo").build()
        );

        //
        GraphQLInvalidSchemeException instance = new GraphQLInvalidSchemeException(
            errors
        );

        // then
        Asserts.assertEquals(instance.getErrors(), errors);
    }
}
