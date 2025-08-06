package com.tvd12.ezyhttp.server.graphql.test;

import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchema;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchemaParser;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

public class GraphQLSchemaParserTest {

    @Test
    public void testStandardize1() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser();
        String query = "{}";

        // when
        GraphQLSchema schema = parser.parseQuery(query);
        Throwable e = Asserts.assertThrows(() -> schema.getQueryDefinitions().get(0).getName());

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 1);
        Asserts.assertEquals(IllegalArgumentException.class, e.getClass());
    }

    @Test
    public void testStandardize2() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser();
        String expectedQueryName = RandomUtil.randomShortAlphabetString();
        String query = "{" + expectedQueryName + "}";

        // when
        GraphQLSchema schema = parser.parseQuery(query);

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 1);
        Asserts.assertEquals(schema.getQueryDefinitions().get(0).getName(), expectedQueryName);
    }

    @Test
    public void testStandardize3() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser();

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
            schemas[i] = parser.parseQuery(queries[i]);
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
        GraphQLSchemaParser parser = new GraphQLSchemaParser();

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
            schemas[i] = parser.parseQuery(queries[i]);
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
        GraphQLSchemaParser parser = new GraphQLSchemaParser();

        // when
        GraphQLSchema schema = parser.parseQuery(null);

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 0);
    }


    @Test
    public void testParseSchema1() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser();

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
            schemas[i] = parser.parseQuery(queries[i]);
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
        GraphQLSchemaParser parser = new GraphQLSchemaParser();

        String query = "{q1 q2}";

        // when
        GraphQLSchema schema = parser.parseQuery(query);

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 2);
        Asserts.assertEquals(schema.getQueryDefinitions().get(0).getName(), "q1");
        Asserts.assertEquals(schema.getQueryDefinitions().get(1).getName(), "q2");
    }

    @Test
    public void testZeroNameLength() {
        // given
        GraphQLSchemaParser parser = new GraphQLSchemaParser();

        String query = "{q1 q2{{}}}";

        // when
        GraphQLSchema schema = parser.parseQuery(query);

        // then
        Asserts.assertEquals(schema.getQueryDefinitions().size(), 2);
        Asserts.assertEquals(schema.getQueryDefinitions().get(0).getName(), "q1");
        Asserts.assertEquals(schema.getQueryDefinitions().get(1).getName(), "q2");
        Asserts.assertEquals(schema.getQueryDefinitions().get(1).getFields().get(0).getName(), null);
        Asserts.assertEquals(schema.getQueryDefinitions().get(1).getFields().get(0).getFields().get(0).getName(), null);
    }
}
