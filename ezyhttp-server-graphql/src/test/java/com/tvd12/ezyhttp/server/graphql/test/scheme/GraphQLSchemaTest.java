package com.tvd12.ezyhttp.server.graphql.test.scheme;

import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;
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

    @Test
    public void addQueryDefinitionTest() {
        // given
        GraphQLQueryDefinition queryDefinition = GraphQLQueryDefinition.builder()
            .name("getUser")
            .build();

        // when
        GraphQLSchema schema = GraphQLSchema.builder()
            .addQueryDefinition(queryDefinition)
            .build();

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 1);
        Asserts.assertEquals(schema.getQueryDefinitions().get(0).getName(), "getUser");
    }

    @Test
    public void addMultipleQueryDefinitionsTest() {
        // given
        GraphQLQueryDefinition queryDefinition1 = GraphQLQueryDefinition.builder()
            .name("getUser")
            .build();
        GraphQLQueryDefinition queryDefinition2 = GraphQLQueryDefinition.builder()
            .name("getProduct")
            .build();

        // when
        GraphQLSchema schema = GraphQLSchema.builder()
            .addQueryDefinition(queryDefinition1)
            .addQueryDefinition(queryDefinition2)
            .build();

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 2);
        Asserts.assertEquals(schema.getQueryDefinitions().get(0).getName(), "getUser");
        Asserts.assertEquals(schema.getQueryDefinitions().get(1).getName(), "getProduct");
    }
}
