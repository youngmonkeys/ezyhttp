package com.tvd12.ezyhttp.server.graphql.test.data;

import com.tvd12.ezyhttp.server.graphql.data.GraphQLDataFilter;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLField;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;

public class GraphQLDataFilterTest {

    private final GraphQLDataFilter instance = new GraphQLDataFilter();

    @Test
    public void filterWithNullDataTest() {
        // given
        GraphQLField childField = GraphQLField.builder()
            .name("name")
            .build();
        GraphQLField queryDefinition = GraphQLField.builder()
            .name("user")
            .addField(childField)
            .build();

        // when
        Map result = instance.filter(null, queryDefinition);

        // then
        Asserts.assertEquals(result, Collections.emptyMap(), false);
    }
}
