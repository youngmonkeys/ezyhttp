package com.tvd12.ezyhttp.server.graphql.test.data;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLError;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class GraphQLErrorTest {

    @Test
    public void test() {
        // given
        String message = RandomUtil.randomShortAlphabetString();
        Map<String, Object> location1 = EzyMapBuilder.mapBuilder()
            .put("hello", 1)
            .toMap();
        Map<String, Object> location2 = EzyMapBuilder.mapBuilder()
            .put("world", 2)
            .toMap();
        Map<String, Object> location3 = EzyMapBuilder.mapBuilder()
            .put("foo", 3)
            .toMap();
        Map<String, Object> location4 = EzyMapBuilder.mapBuilder()
            .put("bar", 4)
            .toMap();
        String path = RandomUtil.randomShortAlphabetString();
        String extensionKey1 = RandomUtil.randomShortAlphabetString();
        String extensionValue1 = RandomUtil.randomShortAlphabetString();
        String extensionKey2 = RandomUtil.randomShortAlphabetString();
        String extensionValue2 = RandomUtil.randomShortAlphabetString();
        String extensionKey3 = RandomUtil.randomShortAlphabetString();
        String extensionValue3 = RandomUtil.randomShortAlphabetString();
        String extensionKey4 = RandomUtil.randomShortAlphabetString();
        String extensionValue4 = RandomUtil.randomShortAlphabetString();

        // when
        GraphQLError instance = GraphQLError.builder()
            .message(message)
            .location(location1)
            .location(location2)
            .locations(Collections.singletonList(location3))
            .locations(Collections.singletonList(location4))
            .path(path)
            .extension(extensionKey1, extensionValue1)
            .extension(extensionKey2, extensionValue2)
            .extensions(
                Collections.singletonMap(extensionKey3, extensionValue3)
            )
            .extensions(
                Collections.singletonMap(extensionKey4, extensionValue4)
            )
            .build();

        // then
        Asserts.assertEquals(
            instance.getMessage(),
            message
        );
        Asserts.assertEquals(
            instance.getLocations(),
            Arrays.asList(
                location1,
                location2,
                location3,
                location4
            ),
            false
        );
        Asserts.assertEquals(
            instance.getPath(),
            Collections.singletonList(path),
            false
        );
        Asserts.assertEquals(
            instance.getExtensions(),
            EzyMapBuilder.mapBuilder()
                .put(extensionKey1, extensionValue1)
                .put(extensionKey2, extensionValue2)
                .put(extensionKey3, extensionValue3)
                .put(extensionKey4, extensionValue4)
                .toMap(),
            false
        );
    }

    @Test
    public void test2() {
        // given
        String message = RandomUtil.randomShortAlphabetString();
        Map<String, Object> location1 = EzyMapBuilder.mapBuilder()
            .put("hello", 1)
            .toMap();
        Map<String, Object> location2 = EzyMapBuilder.mapBuilder()
            .put("world", 2)
            .toMap();
        String path = RandomUtil.randomShortAlphabetString();
        String extensionKey1 = RandomUtil.randomShortAlphabetString();
        String extensionValue1 = RandomUtil.randomShortAlphabetString();
        String extensionKey2 = RandomUtil.randomShortAlphabetString();
        String extensionValue2 = RandomUtil.randomShortAlphabetString();

        // when
        GraphQLError instance = GraphQLError.builder()
            .message(message)
            .locations(Collections.singletonList(location1))
            .locations(Collections.singletonList(location2))
            .path(path)
            .extensions(
                Collections.singletonMap(extensionKey1, extensionValue1)
            )
            .extensions(
                Collections.singletonMap(extensionKey2, extensionValue2)
            )
            .build();

        // then
        Asserts.assertEquals(
            instance.getMessage(),
            message
        );
        Asserts.assertEquals(
            instance.getLocations(),
            Arrays.asList(
                location1,
                location2
            ),
            false
        );
        Asserts.assertEquals(
            instance.getPath(),
            Collections.singletonList(path),
            false
        );
        Asserts.assertEquals(
            instance.getExtensions(),
            EzyMapBuilder.mapBuilder()
                .put(extensionKey1, extensionValue1)
                .put(extensionKey2, extensionValue2)
                .toMap(),
            false
        );
    }
}
