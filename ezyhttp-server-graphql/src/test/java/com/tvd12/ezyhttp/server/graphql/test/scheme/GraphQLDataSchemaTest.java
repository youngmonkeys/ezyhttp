package com.tvd12.ezyhttp.server.graphql.test.scheme;

import com.tvd12.ezyhttp.server.graphql.constants.GraphQLDataFormats;
import com.tvd12.ezyhttp.server.graphql.constants.GraphQLDataTypes;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLDataValue;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLDataSchema;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class GraphQLDataSchemaTest {

    @Test
    public void test() {
        // given
        String name = RandomUtil.randomShortAlphabetString();
        String type = GraphQLDataTypes.BOOLEAN;
        String format = RandomUtil.randomShortAlphabetString();
        String description = RandomUtil.randomShortAlphabetString();

        GraphQLDataSchema items = GraphQLDataSchema.builder()
                .name(RandomUtil.randomShortAlphabetString())
                .type(RandomUtil.randomShortAlphabetString())
                .format(GraphQLDataFormats.DOUBLE)
                .description(RandomUtil.randomShortAlphabetString())
                .build();

        List<GraphQLDataSchema> properties = Collections.singletonList(
            GraphQLDataSchema.builder()
                .name(RandomUtil.randomShortAlphabetString())
                .type(RandomUtil.randomShortAlphabetString())
                .format(RandomUtil.randomShortAlphabetString())
                .description(RandomUtil.randomShortAlphabetString())
                .build()
        );

        List<GraphQLDataValue> examples = Collections.singletonList(
            GraphQLDataValue.builder()
                .name(RandomUtil.randomShortAlphabetString())
                .value(RandomUtil.randomShortAlphabetString())
                .build()
        );

        // when
        GraphQLDataSchema instance = GraphQLDataSchema.builder()
            .name(name)
            .type(type)
            .format(format)
            .description(description)
            .items(items)
            .properties(properties)
            .examples(examples)
            .build();

        // then
        Asserts.assertEquals(instance.getName(), name);
        Asserts.assertEquals(instance.getType(), type);
        Asserts.assertEquals(instance.getFormat(), format);
        Asserts.assertEquals(instance.getDescription(), description);
        Asserts.assertEquals(instance.getExamples(), examples);
        Asserts.assertEquals(instance.getItems(), items);
        Asserts.assertEquals(instance.getProperties(), properties);

        System.out.println(instance);
    }
}
