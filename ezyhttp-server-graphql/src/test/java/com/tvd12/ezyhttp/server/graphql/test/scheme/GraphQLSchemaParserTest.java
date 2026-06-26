package com.tvd12.ezyhttp.server.graphql.test.scheme;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLField;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLObjectMapperException;
import com.tvd12.ezyhttp.server.graphql.json.GraphQLObjectMapperFactory;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchema;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchemaParser;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.MethodInvoker;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
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
    public void standardizeQueryWithLeadingSeparatorTest() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        // when
        GraphQLSchema schema = parser.parseQuery(
            ",{slug}",
            Collections.emptyMap()
        );

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 1);
        Asserts.assertEquals(schema.getQueryDefinitions().get(0).getName(), "slug");
    }

    @Test
    public void standardizeQueryWithTrailingSeparatorTest() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        // when
        GraphQLSchema schema = parser.parseQuery(
            "{slug},",
            Collections.emptyMap()
        );

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 1);
        Asserts.assertEquals(schema.getQueryDefinitions().get(0).getName(), "slug");
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
    public void parseQueryWithStringVariableSpecialChars() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );

        // when
        GraphQLSchema schema = instance.parseQuery(
            "{me(name: $name){}}",
            Collections.singletonMap("name", "he\"l\\lo\nworld")
        );

        // then
        Asserts.assertEquals(
            schema.getQueryDefinitions().get(0).getArguments(),
            EzyMapBuilder.mapBuilder()
                .put("name", "he\"l\\lo\nworld")
                .toMap(),
            false
        );
    }

    @Test
    public void parseQueryWithVariablesInListAndNestedObject() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );

        // when
        GraphQLSchema schema = instance.parseQuery(
            "{me(ids: [$id], filter: {name: $name}){}}",
            EzyMapBuilder.mapBuilder()
                .put("id", 1)
                .put("name", "Dzung")
                .toMap()
        );

        // then
        Asserts.assertEquals(
            schema.getQueryDefinitions().get(0).getArguments(),
            EzyMapBuilder.mapBuilder()
                .put("ids", Collections.singletonList(1))
                .put(
                    "filter",
                    EzyMapBuilder.mapBuilder()
                        .put("name", "Dzung")
                        .toMap()
                )
                .toMap(),
            false
        );
    }

    @Test
    public void replaceVariablePlaceholdersWithNullArgumentsTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "replaceVariablePlaceholders",
            Map.class,
            Map.class
        );
        method.setAccessible(true);

        // when
        method.invoke(
            instance,
            null,
            Collections.singletonMap("id", 1)
        );

        // then
        Asserts.assertTrue(true);
    }

    @Test
    public void replaceVariablePlaceholdersWithNullVariablesTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "replaceVariablePlaceholders",
            Map.class,
            Map.class
        );
        method.setAccessible(true);
        Map<String, Object> arguments = EzyMapBuilder.mapBuilder()
            .put("id", 1)
            .toMap();

        // when
        method.invoke(
            instance,
            arguments,
            null
        );

        // then
        Asserts.assertEquals(
            arguments,
            EzyMapBuilder.mapBuilder()
                .put("id", 1)
                .toMap(),
            false
        );
    }

    @Test
    public void replaceVariablePlaceholdersWithListValueTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "replaceVariablePlaceholders",
            Map.class,
            Map.class
        );
        method.setAccessible(true);
        Map<String, Object> arguments = EzyMapBuilder.mapBuilder()
            .put(
                "ids",
                Arrays.<Object>asList(
                    EzyMapBuilder.mapBuilder()
                        .put("__ezyhttp_graphql_variable__", "id")
                        .toMap()
                )
            )
            .toMap();

        // when
        method.invoke(
            instance,
            arguments,
            Collections.singletonMap("id", 1)
        );

        // then
        Asserts.assertEquals(
            arguments,
            EzyMapBuilder.mapBuilder()
                .put("ids", Collections.singletonList(1))
                .toMap(),
            false
        );
    }

    @Test
    public void replaceVariablePlaceholdersWithNestedListValueTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "replaceVariablePlaceholders",
            Map.class,
            Map.class
        );
        method.setAccessible(true);
        Map<String, Object> arguments = EzyMapBuilder.mapBuilder()
            .put(
                "filters",
                Arrays.<Object>asList(
                    EzyMapBuilder.mapBuilder()
                        .put(
                            "names",
                            Arrays.<Object>asList(
                                EzyMapBuilder.mapBuilder()
                                    .put("__ezyhttp_graphql_variable__", "name")
                                    .toMap()
                            )
                        )
                        .toMap()
                )
            )
            .toMap();

        // when
        method.invoke(
            instance,
            arguments,
            Collections.singletonMap("name", "Dzung")
        );

        // then
        Asserts.assertEquals(
            arguments,
            EzyMapBuilder.mapBuilder()
                .put(
                    "filters",
                    Collections.singletonList(
                        EzyMapBuilder.mapBuilder()
                            .put(
                                "names",
                                Collections.singletonList("Dzung")
                            )
                            .toMap()
                    )
                )
                .toMap(),
            false
        );
    }

    @Test
    public void replaceVariablePlaceholdersShouldPushMapItemInListTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "replaceVariablePlaceholders",
            Map.class,
            Map.class
        );
        method.setAccessible(true);
        Map<String, Object> arguments = EzyMapBuilder.mapBuilder()
            .put(
                "filters",
                Arrays.<Object>asList(
                    EzyMapBuilder.mapBuilder()
                        .put(
                            "name",
                            EzyMapBuilder.mapBuilder()
                                .put("__ezyhttp_graphql_variable__", "name")
                                .toMap()
                        )
                        .toMap()
                )
            )
            .toMap();

        // when
        method.invoke(
            instance,
            arguments,
            Collections.singletonMap("name", "Dzung")
        );

        // then
        Asserts.assertEquals(
            arguments,
            EzyMapBuilder.mapBuilder()
                .put(
                    "filters",
                    Collections.singletonList(
                        EzyMapBuilder.mapBuilder()
                            .put("name", "Dzung")
                            .toMap()
                    )
                )
                .toMap(),
            false
        );
    }

    @Test
    public void replaceVariablePlaceholdersShouldPushListItemInListTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "replaceVariablePlaceholders",
            Map.class,
            Map.class
        );
        method.setAccessible(true);
        Map<String, Object> arguments = EzyMapBuilder.mapBuilder()
            .put(
                "groups",
                Arrays.<Object>asList(
                    Arrays.<Object>asList(
                        EzyMapBuilder.mapBuilder()
                            .put("__ezyhttp_graphql_variable__", "id")
                            .toMap()
                    )
                )
            )
            .toMap();

        // when
        method.invoke(
            instance,
            arguments,
            Collections.singletonMap("id", 1)
        );

        // then
        Asserts.assertEquals(
            arguments,
            EzyMapBuilder.mapBuilder()
                .put(
                    "groups",
                    Collections.singletonList(
                        Collections.singletonList(1)
                    )
                )
                .toMap(),
            false
        );
    }

    @Test
    public void replaceVariablePlaceholdersWithScalarListValueTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "replaceVariablePlaceholders",
            Map.class,
            Map.class
        );
        method.setAccessible(true);
        Map<String, Object> arguments = EzyMapBuilder.mapBuilder()
            .put(
                "tags",
                Arrays.<Object>asList("java", 1, null)
            )
            .toMap();

        // when
        method.invoke(
            instance,
            arguments,
            Collections.singletonMap("id", 1)
        );

        // then
        Asserts.assertEquals(
            arguments,
            EzyMapBuilder.mapBuilder()
                .put(
                    "tags",
                    Arrays.<Object>asList("java", 1, null)
                )
                .toMap(),
            false
        );
    }

    @Test
    public void getVariableValueWithStringVariableNameTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "getVariableValue",
            Object.class,
            Map.class
        );
        method.setAccessible(true);
        Map<String, Object> placeholder = EzyMapBuilder.mapBuilder()
            .put("__ezyhttp_graphql_variable__", "id")
            .toMap();

        // when
        Object result = method.invoke(
            instance,
            placeholder,
            Collections.singletonMap("id", 1)
        );

        // then
        Asserts.assertEquals(result, 1);
    }

    @Test
    public void getVariableValueWithNonStringVariableNameTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "getVariableValue",
            Object.class,
            Map.class
        );
        method.setAccessible(true);
        Map<String, Object> placeholder = EzyMapBuilder.mapBuilder()
            .put("__ezyhttp_graphql_variable__", 1)
            .toMap();

        // when
        Object result = method.invoke(
            instance,
            placeholder,
            Collections.singletonMap("id", 1)
        );

        // then
        Asserts.assertNull(result);
    }

    @Test
    public void parseQuerySubmitWorkReportWithOperationVariables() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );
        String contentJson = "{\"items\":[{\"title\":\"Done\"}]}";
        String scheduleJson = "[{\"time\":\"09:00\",\"task\":\"Daily\"}]";
        String query =
            "query($reportType: String, $reportDate: String, " +
                "$employeeName: String, $username: String, " +
                "$email: String, $department: String, " +
                "$contentJson: String, $scheduleJson: String) {\n" +
                "    submit_work_report(" +
                "reportType: $reportType, " +
                "reportDate: $reportDate, " +
                "employeeName: $employeeName, " +
                "username: $username, " +
                "email: $email, " +
                "department: $department, " +
                "contentJson: $contentJson, " +
                "scheduleJson: $scheduleJson" +
                ") {\n" +
                "        success postId slug message\n" +
                "    }\n" +
                "}";

        // when
        GraphQLSchema schema = instance.parseQuery(
            query,
            EzyMapBuilder.mapBuilder()
                .put("reportType", "daily")
                .put("reportDate", "2026-06-26")
                .put("employeeName", "Dzung")
                .put("username", "dzung@example.com")
                .put("email", "dzung@example.com")
                .put("department", "")
                .put("contentJson", contentJson)
                .put("scheduleJson", scheduleJson)
                .toMap()
        );

        // then
        GraphQLField queryDefinition = schema.getQueryDefinitions().get(0);
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 1);
        Asserts.assertEquals(queryDefinition.getName(), "submit_work_report");
        Asserts.assertEquals(
            queryDefinition.getArguments(),
            EzyMapBuilder.mapBuilder()
                .put("reportType", "daily")
                .put("reportDate", "2026-06-26")
                .put("employeeName", "Dzung")
                .put("username", "dzung@example.com")
                .put("email", "dzung@example.com")
                .put("department", "")
                .put("contentJson", contentJson)
                .put("scheduleJson", scheduleJson)
                .toMap(),
            false
        );
        Asserts.assertEquals(queryDefinition.getFields().size(), 4);
        Asserts.assertEquals(queryDefinition.getFields().get(0).getName(), "success");
        Asserts.assertEquals(queryDefinition.getFields().get(1).getName(), "postId");
        Asserts.assertEquals(queryDefinition.getFields().get(2).getName(), "slug");
        Asserts.assertEquals(queryDefinition.getFields().get(3).getName(), "message");
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

    @Test
    public void parseQueryNoChildBeforeArgumentsTest() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );

        // when
        Throwable e = Asserts.assertThrows(() ->
            instance.parseQuery(
                "(id: 1)",
                Collections.emptyMap()
            )
        );

        // then
        Asserts.assertEqualsType(e, GraphQLObjectMapperException.class);
        GraphQLObjectMapperException exception = (GraphQLObjectMapperException) e;
        Asserts.assertEquals(
            exception.getErrors(),
            EzyMapBuilder.mapBuilder()
                .put("arguments", "invalid")
                .put("message", "there is no child")
                .toMap(),
            false
        );
    }

    @Test
    public void requireParentBuilderWithEmptyStackTest() {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Deque<GraphQLField.Builder> stack = new ArrayDeque<>();

        // when
        final Method method;
        try {
            method = GraphQLSchemaParser.class.getDeclaredMethod(
                "peekFieldStackItemOrThrow",
                Deque.class,
                String.class
            );
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(
                "method requireParentBuilder should exist",
                ex
            );
        }
        method.setAccessible(true);
        Throwable e = Asserts.assertThrows(() -> {
            try {
                method.invoke(
                    instance,
                    stack,
                    "there is no parent case curly brace close"
                );
            } catch (InvocationTargetException ex) {
                throw ex.getCause();
            }
        });

        // then
        Asserts.assertEqualsType(e, GraphQLObjectMapperException.class);
        GraphQLObjectMapperException exception = (GraphQLObjectMapperException) e;
        Asserts.assertEquals(
            exception.getErrors(),
            EzyMapBuilder.mapBuilder()
                .put("arguments", "invalid")
                .put("message", "there is no parent case curly brace close")
                .toMap(),
            false
        );
    }

    @Test
    public void findOperationSelectionStartFromZeroTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "findOperationSelectionStart",
            String.class,
            int.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "{slug}",
            0
        );

        // then
        Asserts.assertEquals(result, 0);
    }

    @Test
    public void findOperationSelectionStartFromLengthTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "findOperationSelectionStart",
            String.class,
            int.class
        );
        method.setAccessible(true);
        String query = "query";

        // when
        Object result = method.invoke(
            instance,
            query,
            query.length()
        );

        // then
        Asserts.assertEquals(result, -1);
    }

    @Test
    public void findOperationSelectionStartShouldUseStartIndexTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "findOperationSelectionStart",
            String.class,
            int.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "{ignored} query {slug}",
            10
        );

        // then
        Asserts.assertEquals(result, 16);
    }

    @Test
    public void findOperationSelectionStartShouldIgnoreBracesInSingleQuotesTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "findOperationSelectionStart",
            String.class,
            int.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query(arg: '{ignored}'){slug}",
            5
        );

        // then
        Asserts.assertEquals(result, 23);
    }

    @Test
    public void findOperationSelectionStartShouldIgnoreBracesInDoubleQuotesTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "findOperationSelectionStart",
            String.class,
            int.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query(arg: \"{ignored}\"){slug}",
            5
        );

        // then
        Asserts.assertEquals(result, 23);
    }

    @Test
    public void findOperationSelectionStartShouldIgnoreEscapedSingleQuoteTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "findOperationSelectionStart",
            String.class,
            int.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query(arg: 'it\\'s {ignored}'){slug}",
            5
        );

        // then
        Asserts.assertEquals(result, 29);
    }

    @Test
    public void findOperationSelectionStartShouldIgnoreEscapedDoubleQuoteTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "findOperationSelectionStart",
            String.class,
            int.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query(arg: \"he\\\"llo {ignored}\"){slug}",
            5
        );

        // then
        Asserts.assertEquals(result, 31);
    }

    @Test
    public void findOperationSelectionStartShouldReturnMinusOneInOpenSingleQuoteTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "findOperationSelectionStart",
            String.class,
            int.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query(arg: '{ignored}){slug}",
            5
        );

        // then
        Asserts.assertEquals(result, -1);
    }

    @Test
    public void findOperationSelectionStartShouldReturnMinusOneInOpenDoubleQuoteTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "findOperationSelectionStart",
            String.class,
            int.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query(arg: \"{ignored}){slug}",
            5
        );

        // then
        Asserts.assertEquals(result, -1);
    }

    @Test
    public void findOperationSelectionStartShouldIgnoreBracesInParenthesesTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "findOperationSelectionStart",
            String.class,
            int.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query(input: {id: 1}){slug}",
            5
        );

        // then
        Asserts.assertEquals(result, 21);
    }

    @Test
    public void findOperationSelectionStartShouldReturnMinusOneWhenBracesOnlyInParenthesesTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "findOperationSelectionStart",
            String.class,
            int.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query(input: {id: 1})",
            5
        );

        // then
        Asserts.assertEquals(result, -1);
    }

    @Test
    public void removeQueryPrefixWithExactPrefixTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "removeQueryPrefix",
            String.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query"
        );

        // then
        Asserts.assertEquals(result, "");
    }

    @Test
    public void removeQueryPrefixWithNonNameCharAfterPrefixTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "removeQueryPrefix",
            String.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query{slug}"
        );

        // then
        Asserts.assertEquals(result, "{slug}");
    }

    @Test
    public void removeQueryPrefixWithOperationNameAndSelectionStartTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "removeQueryPrefix",
            String.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query FindSlug($id: ID){slug}"
        );

        // then
        Asserts.assertEquals(result, "{slug}");
    }

    @Test
    public void removeQueryPrefixWithoutSelectionStartAfterPrefixTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "removeQueryPrefix",
            String.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query FindSlug"
        );

        // then
        Asserts.assertEquals(result, " FindSlug");
    }

    @Test
    public void removeQueryPrefixWithLetterAfterPrefixTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "removeQueryPrefix",
            String.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "queryName{slug}"
        );

        // then
        Asserts.assertEquals(result, "queryName{slug}");
    }

    @Test
    public void removeQueryPrefixWithDigitAfterPrefixTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "removeQueryPrefix",
            String.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query1{slug}"
        );

        // then
        Asserts.assertEquals(result, "query1{slug}");
    }

    @Test
    public void removeQueryPrefixWithUnderscoreAfterPrefixTest() throws Exception {
        // given
        GraphQLSchemaParser instance = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        Method method = GraphQLSchemaParser.class.getDeclaredMethod(
            "removeQueryPrefix",
            String.class
        );
        method.setAccessible(true);

        // when
        Object result = method.invoke(
            instance,
            "query_name{slug}"
        );

        // then
        Asserts.assertEquals(result, "query_name{slug}");
    }
}
