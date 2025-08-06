package com.tvd12.ezyhttp.server.graphql.test.util;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import static com.tvd12.ezyhttp.server.graphql.constants.GraphQLConstants.DEFAULT_QL_GROUP_NAME;
import static com.tvd12.ezyhttp.server.graphql.util.GraphQLQueryGroupExtractors.extractQueryGroup;

public class GraphQLQueryGroupExtractorsTest {

    @Test
    public void extractQueryGroupTest() {
        // given
        // when
        String group = extractQueryGroup(null);

        // then
        Asserts.assertEquals(group, DEFAULT_QL_GROUP_NAME);
    }
}
