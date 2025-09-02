package com.tvd12.ezyhttp.server.graphql.test.controller;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.graphql.controller.GraphQLExceptionHandler;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLError;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLFetcherException;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLInvalidSchemeException;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLObjectMapperException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphQLExceptionHandlerTest {

    @Test
    public void handleGraphQLFetcherExceptionTest() {
        // given
        Map<String, Object> data = new HashMap<>();
        List<GraphQLError> errors = new ArrayList<>();
        GraphQLFetcherException exception = GraphQLFetcherException.builder()
            .data(data)
            .errors(errors)
            .build();

        GraphQLExceptionHandler instance = new GraphQLExceptionHandler();

        // when
        Object actual = instance.handle(exception);

        // then
        Asserts.assertEquals(
            actual,
            EzyMapBuilder.mapBuilder()
                .put("data", data)
                .put("errors", errors)
                .toMap(),
            false
        );
    }

    @Test
    public void handleGraphQLInvalidSchemeExceptionTest() {
        // given
        List<GraphQLError> errors = new ArrayList<>();
        GraphQLInvalidSchemeException exception = new GraphQLInvalidSchemeException(
            errors
        );

        GraphQLExceptionHandler instance = new GraphQLExceptionHandler();

        // when
        Object actual = instance.handle(exception);

        // then
        Asserts.assertEquals(
            actual,
            errors
        );
    }

    @Test
    public void handleGraphQLObjectMapperExceptionTest() {
        // given
        List<GraphQLError> errors = new ArrayList<>();
        GraphQLObjectMapperException exception = new GraphQLObjectMapperException(
            errors,
            new Exception("test")
        );

        GraphQLExceptionHandler instance = new GraphQLExceptionHandler();

        // when
        Object actual = instance.handle(exception);

        // then
        Asserts.assertEquals(
            actual,
            errors
        );
    }
}
