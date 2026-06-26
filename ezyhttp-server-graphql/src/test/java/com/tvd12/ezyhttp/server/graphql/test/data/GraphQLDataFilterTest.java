package com.tvd12.ezyhttp.server.graphql.test.data;

import com.tvd12.ezyhttp.server.graphql.data.GraphQLDataFilter;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLField;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLInvalidSchemeException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.tvd12.ezyfox.util.EzyMapBuilder.mapBuilder;
import static com.tvd12.ezyhttp.server.graphql.constants.GraphQLConstants.ALL_FIELDS;

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

    @Test
    public void filterWithAllFieldsTest() {
        // given
        GraphQLField queryDefinition = GraphQLField.builder()
            .name("user")
            .addField(
                GraphQLField.builder()
                    .name(ALL_FIELDS)
                    .build()
            )
            .build();
        Map data = mapBuilder()
            .put("id", 1)
            .put("name", "Dzung")
            .put("nullable", null)
            .toMap();

        // when
        Map result = instance.filter(data, queryDefinition);

        // then
        Asserts.assertEquals(
            result,
            mapBuilder()
                .put("id", 1)
                .put("name", "Dzung")
                .toMap(),
            false
        );
    }

    @Test
    public void filterWithAllFieldsAndNestedOverrideTest() {
        // given
        GraphQLField queryDefinition = GraphQLField.builder()
            .name("user")
            .addField(
                GraphQLField.builder()
                    .name(ALL_FIELDS)
                    .build()
            )
            .addField(
                GraphQLField.builder()
                    .name("friends")
                    .addField(
                        GraphQLField.builder()
                            .name("name")
                            .build()
                    )
                    .build()
            )
            .build();
        Map data = mapBuilder()
            .put("id", 1)
            .put(
                "friends",
                Arrays.asList(
                    mapBuilder()
                        .put("id", 2)
                        .put("name", "Foo")
                        .toMap(),
                    mapBuilder()
                        .put("id", 3)
                        .put("name", "Bar")
                        .toMap()
                )
            )
            .toMap();

        // when
        Map result = instance.filter(data, queryDefinition);

        // then
        Asserts.assertEquals(
            result,
            mapBuilder()
                .put("id", 1)
                .put(
                    "friends",
                    Arrays.asList(
                        mapBuilder()
                            .put("name", "Foo")
                            .toMap(),
                        mapBuilder()
                            .put("name", "Bar")
                            .toMap()
                    )
                )
                .toMap(),
            false
        );
    }

    @Test
    public void filterListWithAllFieldsTest() {
        // given
        GraphQLField queryDefinition = GraphQLField.builder()
            .name("friends")
            .addField(
                GraphQLField.builder()
                    .name(ALL_FIELDS)
                    .build()
            )
            .build();

        // when
        Object result = instance.filterList(
            Arrays.asList(
                new HashMap<>(
                    mapBuilder()
                        .put("id", 1)
                        .put("name", "Foo")
                        .toMap()
                ),
                new HashMap<>(
                    mapBuilder()
                        .put("id", 2)
                        .put("name", "Bar")
                        .toMap()
                )
            ),
            queryDefinition
        );

        // then
        Asserts.assertEquals(
            result,
            Arrays.asList(
                mapBuilder()
                    .put("id", 1)
                    .put("name", "Foo")
                    .toMap(),
                mapBuilder()
                    .put("id", 2)
                    .put("name", "Bar")
                    .toMap()
            ),
            false
        );
    }

    @Test
    public void filterListFieldWithNonMapItemTest() {
        // given
        GraphQLField queryDefinition = GraphQLField.builder()
            .name("user")
            .addField(
                GraphQLField.builder()
                    .name("tags")
                    .addField(
                        GraphQLField.builder()
                            .name("name")
                            .build()
                    )
                    .build()
            )
            .build();
        Map data = mapBuilder()
            .put("tags", Arrays.asList("java", "graphql"))
            .toMap();

        // when
        Throwable e = Asserts.assertThrows(() ->
            instance.filter(data, queryDefinition)
        );

        // then
        Asserts.assertEqualsType(e, GraphQLInvalidSchemeException.class);
        Asserts.assertEquals(
            ((GraphQLInvalidSchemeException) e).getErrors(),
            mapBuilder()
                .put("schema", "invalid")
                .put("field", "tags")
                .toMap(),
            false
        );
    }
}
