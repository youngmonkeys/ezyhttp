package com.tvd12.ezyhttp.server.graphql.test.data;

import com.tvd12.ezyhttp.server.graphql.data.GraphQLDataValue;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

public class GraphQLDataValueTest {

    @Test
    public void test() {
        // given
        String name = RandomUtil.randomShortAlphabetString();
        Object value = RandomUtil.randomShortAlphabetString();

        // when
        GraphQLDataValue instance = GraphQLDataValue.builder()
            .name(name)
            .value(value)
            .build();

        // then
        Asserts.assertEquals(instance.getName(), name);
        Asserts.assertEquals(instance.getValue(), value);
    }
}
