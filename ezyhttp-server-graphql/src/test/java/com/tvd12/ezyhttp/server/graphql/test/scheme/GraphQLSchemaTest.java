package com.tvd12.ezyhttp.server.graphql.test.scheme;

import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchema;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Collections;

public class GraphQLSchemaTest {

    @Test
    public void test() {
        // given
        GraphQLSchema.Builder builder = GraphQLSchema.builder();

        // when
        GraphQLSchema schema = builder.build();

        // then
        Asserts.assertEquals(schema.getQueryDefinitions(), Collections.emptyList());
    }
}
