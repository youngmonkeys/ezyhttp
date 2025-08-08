package com.tvd12.ezyhttp.server.graphql.test.data;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLField;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.util.Collections;

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
        Asserts.assertTrue(fieldA.equals(fieldA));
        Asserts.assertFalse(fieldA.equals(new Object()));
        Asserts.assertEquals(fieldA.toString(), fieldName + ", []");
        Asserts.assertEquals(fieldB.toString(), fieldName + ", [" + fieldA + "]");
        Asserts.assertEquals(fieldA.hashCode(), fieldName.hashCode());
        Asserts.assertEquals(
            fieldB.getFieldByName(),
            Collections.singletonMap(fieldName, fieldA),
            false
        );
        Asserts.assertNull(fieldA.getArgumentValue("alo"));
        Asserts.assertNull(fieldA.getArgumentValue("alo", String.class));
        Asserts.assertNull(fieldA.getFieldArgumentValue("alo", fieldName));
        Asserts.assertNull(fieldA.getFieldArgumentValue("alo", String.class, fieldName));
    }

    @Test
    public void getArgumentValueByNameAndTypeTest() {
        // given
        String fieldName = RandomUtil.randomShortAlphabetString();
        GraphQLField.Builder builder = GraphQLField.builder()
            .name(fieldName)
            .arguments(
                EzyMapBuilder.mapBuilder()
                    .put("hello", 1)
                    .put("world", "2")
                    .toMap()
            );
        GraphQLField fieldA = builder.build();

        // when
        // then
        Asserts.assertEquals(
            fieldA.getArgumentValue("hello", long.class),
            1L
        );
        Asserts.assertEquals(
            fieldA.getArgumentValue("world", long.class),
            2L
        );
        System.out.println(builder);
        System.out.println(fieldA);
    }

    @Test
    public void toStringTest() {
        // given
        // when
        // then
        System.out.println(
            GraphQLField.builder()
            .name("hello")
            .arguments(Collections.emptyMap())
        );
    }
}
