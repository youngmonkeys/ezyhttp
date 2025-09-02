package com.tvd12.ezyhttp.server.graphql.test.exception;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLError;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLFetcherException;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class GraphQLFetcherExceptionTest {

    @Test
    public void test() {
        // given
        GraphQLError error1 = GraphQLError.builder().build();
        GraphQLError error2 = GraphQLError.builder().build();
        GraphQLError error3 = GraphQLError.builder().build();

        // when
        GraphQLFetcherException instance = GraphQLFetcherException.builder()
            .errorDataFieldAndPaths("errorField1")
            .errorDataFieldAndPaths("errorField2", "f2")
            .errorDataFieldAndPaths(
                "errorField3",
                "hello",
                "world",
                "foo",
                "bar"
            )
            .error(error1)
            .error(error2)
            .errors(Collections.singletonList(error3))
            .build();

        // then
        Asserts.assertEquals(
            instance.toDataMap(),
            EzyMapBuilder.mapBuilder()
                .put("errors", Arrays.asList(error1, error2, error3))
                .put(
                    "data",
                    EzyMapBuilder.mapBuilder()
                        .put("errorField1", null)
                        .put(
                            "f2",
                            EzyMapBuilder.mapBuilder()
                                .put("errorField2", null)
                                .toMap()
                        )
                        .put(
                            "hello",
                            EzyMapBuilder.mapBuilder()
                                .put(
                                    "world",
                                    EzyMapBuilder.mapBuilder()
                                        .put(
                                            "foo",
                                            EzyMapBuilder.mapBuilder()
                                                .put(
                                                    "bar",
                                                    EzyMapBuilder.mapBuilder()
                                                        .put("errorField3", null)
                                                        .toMap()
                                                )
                                                .toMap()
                                        )
                                        .toMap()
                                )
                                .toMap()
                        )
                        .toMap()
                )
                .toMap(),
            false
        );
    }

    @Test
    public void listTest() {
        // given
        GraphQLError error1 = GraphQLError.builder().build();
        GraphQLError error2 = GraphQLError.builder().build();
        Map<String, Object> data = EzyMapBuilder.mapBuilder()
            .put("hello", null)
            .put(
                "world",
                EzyMapBuilder.mapBuilder()
                    .put("foo", null)
                    .toMap()
            )
            .toMap();

        // when
        GraphQLFetcherException instance = GraphQLFetcherException.builder()
            .errors(Collections.singletonList(error1))
            .errors(Collections.singletonList(error2))
            .data(data)
            .data(data)
            .errorDataFieldAndPaths("foo", "world")
            .build();

        // then
        Asserts.assertEquals(
            instance.toDataMap(),
            EzyMapBuilder.mapBuilder()
                .put("errors", Arrays.asList(error1, error2))
                .put(
                    "data",
                    EzyMapBuilder.mapBuilder()
                        .put("hello", null)
                        .put(
                            "world",
                            EzyMapBuilder.mapBuilder()
                                .put("foo", null)
                                .toMap()
                        )
                        .toMap()
                )
                .toMap(),
            false
        );
        Asserts.assertEquals(
            instance.getErrors(),
            Arrays.asList(error1, error2),
            false
        );
        Asserts.assertEquals(
            instance.getData(),
            EzyMapBuilder.mapBuilder()
                .put("hello", null)
                .put(
                    "world",
                    EzyMapBuilder.mapBuilder()
                        .put("foo", null)
                        .toMap()
                )
                .toMap(),
            false
        );
    }

    @Test
    public void dataNullTest() {
        // given
        GraphQLError error1 = GraphQLError.builder().build();
        GraphQLError error2 = GraphQLError.builder().build();

        // when
        GraphQLFetcherException instance = GraphQLFetcherException.builder()
            .errors(Collections.singletonList(error1))
            .errors(Collections.singletonList(error2))
            .build();

        // then
        Asserts.assertEquals(
            instance.toDataMap(),
            EzyMapBuilder.mapBuilder()
                .put("errors", Arrays.asList(error1, error2))
                .toMap(),
            false
        );
        Asserts.assertNull(instance.getData());
    }
}
