package com.tvd12.ezyhttp.server.graphql.test.scheme;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLObjectMapperException;
import com.tvd12.ezyhttp.server.graphql.json.GraphQLObjectMapperFactory;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchema;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchemaParser;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.MethodInvoker;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class GraphQLSchemaParserTest {

    @Test
    public void testStandardize1() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        String query = "{}";

        // when
        GraphQLSchema schema = parser.parseQuery(query, Collections.emptyMap());
        System.out.println(schema.getQueryDefinitions().get(0).getName());

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 1);
    }

    @Test
    public void testStandardize2() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        String expectedQueryName = RandomUtil.randomShortAlphabetString();
        String query = "{" + expectedQueryName + "}";

        // when
        GraphQLSchema schema = parser.parseQuery(query, Collections.emptyMap());

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 1);
        Asserts.assertEquals(schema.getQueryDefinitions().get(0).getName(), expectedQueryName);
    }

    @Test
    public void testStandardize3() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        String[] queries = {
            "{queryName}",
            "{ queryName}",
            "{queryName }",
            "{ queryName }",
            "{ \tqueryName\t}",
            "{ \tqueryName\t }",
            "{\t queryName \t}",
            "{ \t  \t   queryName    \t \t \t\t\t}",
            "{\n\tqueryName\n}",
            "{ queryName, }",
            "{ queryName+ }",
            "{ queryName\t }",
        };
        int numQueries = queries.length;
        GraphQLSchema[] schemas = new GraphQLSchema[numQueries];

        // when
        for (int i = 0; i < numQueries; ++i) {
            schemas[i] = parser.parseQuery(queries[i], Collections.emptyMap());
        }
        // then
        for (int i = 0; i < numQueries; ++i) {
            Asserts.assertEquals(schemas[i].getQueryDefinitions().size(), 1);
            Asserts.assertEquals(schemas[i].getQueryDefinitions().get(0).getName(), "queryName");
        }
    }

    @Test
    public void testStandardize4() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        String[] queries = {
            "{queryName{field1 field2}}",
            "{queryName{field1+field2}}",
            "{queryName{field1,field2}}",
            " { queryName { field1 field2 } } ",
            " {\n\tqueryName {\n\t\tfield1\n\t\tfield2\n\t}\n}",
            "{ queryName  \t{field1\t \n \t + \n \t field2}  \t\n\t}",
            "query { queryName  \t{field1\t \n \t + \n \t field2}  \t\n\t}",
        };
        int numQueries = queries.length;
        GraphQLSchema[] schemas = new GraphQLSchema[numQueries];

        // when
        for (int i = 0; i < numQueries; ++i) {
            schemas[i] = parser.parseQuery(queries[i], Collections.emptyMap());
        }
        // then
        for (int i = 0; i < numQueries; ++i) {
            Asserts.assertEquals(schemas[i].getQueryDefinitions().size(), 1);
            Asserts.assertEquals(schemas[i].getQueryDefinitions().get(0).getName(), "queryName");
            Asserts.assertEquals(schemas[i].getQueryDefinitions().get(0).getFields().size(), 2);
        }
    }

    @Test
    public void testStandardize5() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        // when
        GraphQLSchema schema = parser.parseQuery(null, Collections.emptyMap());

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 0);
    }


    @Test
    public void testParseSchema1() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        String[] queries = {
            "{q1{f1 f2{f21} f3{f31 f32}} q2}",
            "{q1{f1+f2{f21}+f3{f31+f32}}+q2}",
            "{q1{f1,f2{f21},f3{f31,f32}},q2}",
            "{q1{f1\tf2{f21}\tf3{f31\tf32}}\tq2}",
            "{q1{f1\nf2{f21}\nf3{f31\nf32}}\nq2}",
        };

        int numQueries = queries.length;
        GraphQLSchema[] schemas = new GraphQLSchema[numQueries];

        // when
        for (int i = 0; i < numQueries; ++i) {
            schemas[i] = parser.parseQuery(queries[i], Collections.emptyMap());
        }

        // then
        for (int i = 0; i < numQueries; ++i) {
            Asserts.assertEquals(schemas[i].getQueryDefinitions().size(), 2);
            Asserts.assertEquals(schemas[i].getQueryDefinitions().get(0).getName(), "q1");
            Asserts.assertEquals(schemas[i].getQueryDefinitions().get(1).getName(), "q2");
            Asserts.assertEquals(schemas[i].getQueryDefinitions().get(0).getFields().size(), 3);
            Asserts.assertEquals(schemas[i].getQueryDefinitions().get(0).getFields().get(0).getName(), "f1");
            Asserts.assertEquals(schemas[i].getQueryDefinitions().get(0).getFields().get(1).getName(), "f2");
            Asserts.assertEquals(schemas[i].getQueryDefinitions().get(0).getFields().get(2).getName(), "f3");
            Asserts.assertEquals(schemas[i].getQueryDefinitions().get(0).getFields().get(2).getFields().get(0).getName(), "f31");
            Asserts.assertEquals(schemas[i].getQueryDefinitions().get(0).getFields().get(2).getFields().get(1).getName(), "f32");
        }
    }

    @Test
    public void testParseSchema2() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        String query = "{q1 q2}";

        // when
        GraphQLSchema schema = parser.parseQuery(query, Collections.emptyMap());

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 2);
        Asserts.assertEquals(schema.getQueryDefinitions().get(0).getName(), "q1");
        Asserts.assertEquals(schema.getQueryDefinitions().get(1).getName(), "q2");
    }

    @Test
    public void testZeroNameLength() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        String query = "{q1 q2{{}}}";

        // when
        GraphQLSchema schema = parser.parseQuery(query, Collections.emptyMap());

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 2);
        Asserts.assertEquals(schema.getQueryDefinitions().get(0).getName(), "q1");
        Asserts.assertEquals(schema.getQueryDefinitions().get(1).getName(), "q2");
        Asserts.assertEquals(schema.getQueryDefinitions().get(1).getFields().get(0).getName(), null);
        Asserts.assertEquals(schema.getQueryDefinitions().get(1).getFields().get(0).getFields().get(0).getName(), null);
    }

    @Test
    public void testWithArguments() {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        GraphQLSchemaParser parser = new GraphQLSchemaParser(
            objectMapper
        );

        String query = "{q1(hello: 1) q2(world: 2, foo: {\"Hello\": \"World\"}, bar:[1, 2, 3]){value(animal: 1){}}}";

        // when
        GraphQLSchema schema = parser.parseQuery(query, Collections.emptyMap());

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 2);
        Asserts.assertEquals(schema.getQueryDefinitions().get(0).getName(), "q1");
        Asserts.assertEquals(schema.getQueryDefinitions().get(1).getName(), "q2");
        Asserts.assertEquals(schema.getQueryDefinitions().get(1).getFields().get(0).getName(), "value");
        Asserts.assertEquals(schema.getQueryDefinitions().get(1).getFields().get(0).getFields().get(0).getName(), null);
        Asserts.assertEquals(
            schema.getQueryDefinitions().get(0).getArguments(),
            EzyMapBuilder.mapBuilder()
                .put("hello", 1)
                .toMap(),
            false
        );
        Asserts.assertEquals(
            schema.getQueryDefinitions().get(1).getArguments(),
            EzyMapBuilder.mapBuilder()
                .put("world", 2)
                .put(
                    "foo",
                    EzyMapBuilder.mapBuilder()
                        .put("Hello", "World")
                        .toMap()
                )
                .put(
                    "bar",
                    Arrays.asList(1, 2, 3)
                )
                .toMap(),
            false
        );
        Asserts.assertEquals(
            schema.getQueryDefinitions().get(1).getFields().get(0).getArguments(),
            EzyMapBuilder.mapBuilder()
                .put("animal", 1)
                .toMap(),
            false
        );
    }

    @Test
    public void parseQueryNormal() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );

        // when
        GraphQLSchema schema = instance.parseQuery(
            "{me(id: 1){}}",
            Collections.emptyMap()
        );

        // then
        Asserts.assertEquals(
            schema.getQueryDefinitions().get(0).getArguments(),
            EzyMapBuilder.mapBuilder()
                .put("id", 1)
                .toMap(),
            false
        );
    }

    @Test
    public void parseQueryNormalWithArgs() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );

        // when
        GraphQLSchema schema = instance.parseQuery(
            "{me(id:   1, name: {'value' : \"hello\"}, age: {value: $variable  , level:  $level   }){}}",
            EzyMapBuilder.mapBuilder()
                .put("variable", 33)
                .put("level", 2025)
                .toMap()
        );

        // then
        Asserts.assertEquals(
            schema.getQueryDefinitions().get(0).getArguments(),
            EzyMapBuilder.mapBuilder()
                .put("id", 1)
                .put(
                    "name",
                    EzyMapBuilder.mapBuilder()
                        .put("value", "hello")
                        .toMap()
                )
                .put(
                    "age",
                    EzyMapBuilder.mapBuilder()
                        .put("value", 33)
                        .put("level", 2025)
                        .toMap()
                )
                .toMap(),
            false
        );
    }

    @Test
    public void parseQueryNormalWithArgsSpecialCase() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );

        // when
        GraphQLSchema schema = instance.parseQuery(
            "{me(id:   1, name: {'va\\\'lue' : \"he\\\"llo\"}, age: {value: $variable  , level:  $level   }){}}",
            EzyMapBuilder.mapBuilder()
                .put("variable", 33)
                .put("level", 2025)
                .toMap()
        );

        // then
        Asserts.assertEquals(
            schema.getQueryDefinitions().get(0).getArguments(),
            EzyMapBuilder.mapBuilder()
                .put("id", 1)
                .put(
                    "name",
                    EzyMapBuilder.mapBuilder()
                        .put("va\'lue", "he\"llo")
                        .toMap()
                )
                .put(
                    "age",
                    EzyMapBuilder.mapBuilder()
                        .put("value", 33)
                        .put("level", 2025)
                        .toMap()
                )
                .toMap(),
            false
        );
    }

    @Test
    public void parseQueryInvalidArgumentTest() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        // when
        Throwable e = Asserts.assertThrows(() ->
            instance.parseQuery(
                "{me(hello: world){}}",
                Collections.emptyMap()
            )
        );

        // then
        Asserts.assertEqualsType(e, GraphQLObjectMapperException.class);
    }

    @Test
    public void parseQueryInvalidArgument2Test() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        // when
        Throwable e = Asserts.assertThrows(() ->
            instance.parseQuery(
                "{me(hello: world}",
                Collections.emptyMap()
            )
        );

        // then
        Asserts.assertEqualsType(e, GraphQLObjectMapperException.class);
    }

    @Test
    public void parseQueryInvalidArgument3Test() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        // when
        Throwable e = Asserts.assertThrows(() ->
            instance.parseQuery(
                "{me(hello: $world",
                Collections.singletonMap("hello", "world")
            )
        );

        // then
        Asserts.assertEqualsType(e, GraphQLObjectMapperException.class);
    }

    @Test
    public void parseQueryInvalidArgument3xTest() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        StringBuilder argumentsBuilder = new StringBuilder();
        String query = "{me(hello: $";
        int queryLength = query.length();

        // when
        Integer i = MethodInvoker.create()
            .object(instance)
            .method("extractQueryArguments")
            .param(StringBuilder.class, argumentsBuilder)
            .param(String.class, query)
            .param(int.class, 0)
            .param(int.class, queryLength)
            .param(Map.class, Collections.singletonMap("hello", "world"))
            .invoke(Integer.class);

        // then
        Asserts.assertEquals(i, queryLength + 1);
    }

    @Test
    public void parseQueryInvalidArgument4Test() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        // when
        Throwable e = Asserts.assertThrows(() ->
            instance.parseQuery(
                "{me(hello: '\"world)",
                Collections.emptyMap()
            )
        );

        // then
        Asserts.assertEqualsType(e, GraphQLObjectMapperException.class);
    }

    @Test
    public void parseQueryInvalidArgument5Test() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        // when
        Throwable e = Asserts.assertThrows(() ->
            instance.parseQuery(
                "{me(hello: 'world)",
                Collections.emptyMap()
            )
        );

        // then
        Asserts.assertEqualsType(e, GraphQLObjectMapperException.class);
    }

    @Test
    public void parseQueryInvalidArgument6Test() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        // when
        Throwable e = Asserts.assertThrows(() ->
            instance.parseQuery(
                "{me(hello: \"world)",
                Collections.emptyMap()
            )
        );

        // then
        Asserts.assertEqualsType(e, GraphQLObjectMapperException.class);
    }
}
