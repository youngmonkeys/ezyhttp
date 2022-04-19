package com.tvd12.ezyhttp.server.graphql.test;

import com.tvd12.ezyhttp.server.graphql.GraphQLField;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

public class GraphQLFieldTest {

    @Test
    public void test() {
        // given
        String fieldName = RandomUtil.randomShortAlphabetString();
        GraphQLField fieldA = GraphQLField.builder()
                .name(fieldName)
                .build();

        GraphQLField fieldB = GraphQLField.builder()
                .name(fieldName)
                .addField(fieldA)
                .build();

        // when
        boolean isEqual = fieldA.equals(fieldB);

        // then
        Asserts.assertTrue(isEqual);
        Asserts.assertEquals(fieldA.toString(), fieldName + ", []");
        Asserts.assertEquals(fieldB.toString(), fieldName + ", [" + fieldA + "]");
        Asserts.assertEquals(fieldA.hashCode(), fieldName.hashCode());
    }

}
