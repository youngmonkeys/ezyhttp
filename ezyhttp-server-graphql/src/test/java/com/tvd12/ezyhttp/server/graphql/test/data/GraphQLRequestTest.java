package com.tvd12.ezyhttp.server.graphql.test.data;

import com.tvd12.ezyhttp.server.graphql.data.GraphQLRequest;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GraphQLRequestTest {

    @Test
    public void getVariablesReturnsEmptyMapWhenUnset() {
        // given
        GraphQLRequest request = new GraphQLRequest();

        // when
        Map<String, Object> variables = request.getVariables();

        // then
        Asserts.assertEquals(variables, Collections.emptyMap(), false);
        Asserts.assertTrue(variables.isEmpty());
    }

    @Test
    public void getVariablesReturnsProvidedMap() {
        // given
        GraphQLRequest request = new GraphQLRequest();
        Map<String, Object> provided = new HashMap<>();
        provided.put("hello", "world");
        request.setVariables(provided);

        // when
        Map<String, Object> variables = request.getVariables();

        // then
        Asserts.assertEquals(variables, provided, false);
        Asserts.assertEquals(variables.get("hello"), "world");
    }
}
