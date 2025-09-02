package com.tvd12.ezyhttp.server.graphql.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.exception.EzyNotImplementedException;
import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;
import com.tvd12.ezyhttp.core.exception.HttpInternalServerErrorException;
import com.tvd12.ezyhttp.core.exception.HttpNotAcceptableException;
import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.controller.GraphQLController;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLDataFilter;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLRequest;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLInvalidSchemeException;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcherManager;
import com.tvd12.ezyhttp.server.graphql.interceptor.GraphQLInterceptor;
import com.tvd12.ezyhttp.server.graphql.interceptor.GraphQLInterceptorManager;
import com.tvd12.ezyhttp.server.graphql.json.GraphQLObjectMapperFactory;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;
import com.tvd12.ezyhttp.server.graphql.scheme.GraphQLSchemaParser;
import com.tvd12.ezyhttp.server.graphql.test.datafetcher.*;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.*;

public class GraphQLControllerTest {

    @Test
    public void test() throws Exception {
        // given
        GraphQLSchemaParser schemaParser = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        GraphQLDataFetcher meDataFetcher = new GraphQLMeDataFetcher();
        GraphQLDataFetcher heroDataFetcher = new GraphQLHeroDataFetcher();
        GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder()
            .addDataFetcher(meDataFetcher)
            .addDataFetcher(heroDataFetcher)
            .build();
        ObjectMapper objectMapper = new ObjectMapper();

        RequestArguments arguments = mock(RequestArguments.class);
        GraphQLInterceptor interceptor = mock(GraphQLInterceptor.class);
        when(
            interceptor.preHandle(
                any(RequestArguments.class),
                any(String.class),
                any(String.class),
                any(GraphQLQueryDefinition.class),
                any(GraphQLDataFetcher.class)
            )
        ).thenReturn(true);

        GraphQLInterceptorManager interceptorManager = mock(GraphQLInterceptorManager.class);
        when(interceptorManager.getRequestInterceptors()).thenReturn(
            Collections.singletonList(interceptor)
        );

        GraphQLController controller = GraphQLController.builder()
            .schemaParser(schemaParser)
            .dataFilter(new GraphQLDataFilter())
            .dataFetcherManager(dataFetcherManager)
            .objectMapper(objectMapper)
            .interceptorManager(interceptorManager)
            .build();

        GraphQLRequest meRequest = new GraphQLRequest();
        meRequest.setQuery("query{    me   {     name bank{id} friends{name} address}}");

        String heroQuery = "{hero}";

        // when
        Object meResult = controller.doPost(arguments, meRequest);
        Throwable e = Asserts.assertThrows(() ->
            controller.doGet(arguments, heroQuery, null)
        );

        // then
        Asserts.assertFalse(controller.isAuthenticated());
        Asserts.assertEquals(meResult.toString(), "{me={bank={id=100}, name=Dzung, friends=[{name=Foo}, {name=Bar}]}}");
        Asserts.assertEqualsType(e, HttpInternalServerErrorException.class);

        verify(interceptorManager, times(2)).getRequestInterceptors();
        verifyNoMoreInteractions(interceptorManager);

        verify(interceptor, times(2)).preHandle(
            any(RequestArguments.class),
            any(String.class),
            any(String.class),
            any(GraphQLQueryDefinition.class),
            any(GraphQLDataFetcher.class)
        );
        verify(interceptor, times(1)).postHandle(
            any(RequestArguments.class),
            any(String.class),
            any(String.class),
            any(GraphQLQueryDefinition.class),
            any(Object.class),
            any(GraphQLDataFetcher.class)
        );
        verifyNoMoreInteractions(interceptor);

        verify(arguments, times(6)).setArgument(
            any(Object.class),
            any(Object.class)
        );
        verifyNoMoreInteractions(arguments);
    }

    @Test
    public void getAllFieldsTest() {
        // given
        GraphQLSchemaParser schemaParser = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        GraphQLDataFetcher meDataFetcher = new GraphQLMeDataFetcher();
        GraphQLDataFetcher heroDataFetcher = new GraphQLHeroDataFetcher();
        GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder()
            .addDataFetcher(meDataFetcher)
            .addDataFetcher(heroDataFetcher)
            .build();
        ObjectMapper objectMapper = new ObjectMapper();

        RequestArguments arguments = mock(RequestArguments.class);
        GraphQLInterceptor interceptor = mock(GraphQLInterceptor.class);
        when(
            interceptor.preHandle(
                any(RequestArguments.class),
                any(String.class),
                any(String.class),
                any(GraphQLQueryDefinition.class),
                any(GraphQLDataFetcher.class)
            )
        ).thenReturn(true);

        GraphQLInterceptorManager interceptorManager = mock(GraphQLInterceptorManager.class);
        when(interceptorManager.getRequestInterceptors()).thenReturn(
            Collections.singletonList(interceptor)
        );

        GraphQLController controller = GraphQLController.builder()
            .schemaParser(schemaParser)
            .dataFilter(new GraphQLDataFilter())
            .dataFetcherManager(dataFetcherManager)
            .objectMapper(objectMapper)
            .interceptorManager(interceptorManager)
            .build();

        GraphQLRequest meRequest = new GraphQLRequest();
        meRequest.setQuery("query{me{*}}");

        // when
        Object meResult = controller.doPost(arguments, meRequest);

        // then
        Asserts.assertFalse(controller.isAuthenticated());
        Asserts.assertEquals(meResult.toString(), "{me={bank={id=100}, nickName=Hello, name=Dzung, id=1, friends=[{id=1, name=Foo}, {id=1, name=Bar}]}}");

        verify(interceptorManager, times(1)).getRequestInterceptors();
        verifyNoMoreInteractions(interceptorManager);

        verify(interceptor, times(1)).preHandle(
            any(RequestArguments.class),
            any(String.class),
            any(String.class),
            any(GraphQLQueryDefinition.class),
            any(GraphQLDataFetcher.class)
        );
        verify(interceptor, times(1)).postHandle(
            any(RequestArguments.class),
            any(String.class),
            any(String.class),
            any(GraphQLQueryDefinition.class),
            any(Object.class),
            any(GraphQLDataFetcher.class)
        );
        verifyNoMoreInteractions(interceptor);

        verify(arguments, times(3)).setArgument(
            any(Object.class),
            any(Object.class)
        );
        verifyNoMoreInteractions(arguments);
    }

    @Test
    public void getAllFriendFields() {
        // given
        GraphQLSchemaParser schemaParser = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        GraphQLDataFetcher meDataFetcher = new GraphQLMeDataFetcher();
        GraphQLDataFetcher heroDataFetcher = new GraphQLHeroDataFetcher();
        GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder()
            .addDataFetcher(meDataFetcher)
            .addDataFetcher(heroDataFetcher)
            .build();
        ObjectMapper objectMapper = new ObjectMapper();

        RequestArguments arguments = mock(RequestArguments.class);
        GraphQLInterceptor interceptor = mock(GraphQLInterceptor.class);
        when(
            interceptor.preHandle(
                any(RequestArguments.class),
                any(String.class),
                any(String.class),
                any(GraphQLQueryDefinition.class),
                any(GraphQLDataFetcher.class)
            )
        ).thenReturn(true);

        GraphQLInterceptorManager interceptorManager = mock(GraphQLInterceptorManager.class);
        when(interceptorManager.getRequestInterceptors()).thenReturn(
            Collections.singletonList(interceptor)
        );

        GraphQLController controller = GraphQLController.builder()
            .schemaParser(schemaParser)
            .dataFilter(new GraphQLDataFilter())
            .dataFetcherManager(dataFetcherManager)
            .objectMapper(objectMapper)
            .interceptorManager(interceptorManager)
            .build();

        GraphQLRequest meRequest = new GraphQLRequest();
        meRequest.setQuery("query{me{name bank{id} friends{*}}}");

        String heroQuery = "{hero}";

        // when
        Object meResult = controller.doPost(arguments, meRequest);
        Throwable e = Asserts.assertThrows(() ->
            controller.doGet(arguments, heroQuery, null)
        );

        // then
        Asserts.assertFalse(controller.isAuthenticated());
        Asserts.assertEquals(meResult.toString(), "{me={bank={id=100}, name=Dzung, friends=[{name=Foo, id=1}, {name=Bar, id=1}]}}");
        Asserts.assertEqualsType(e, HttpInternalServerErrorException.class);

        verify(interceptorManager, times(2)).getRequestInterceptors();
        verifyNoMoreInteractions(interceptorManager);

        verify(interceptor, times(2)).preHandle(
            any(RequestArguments.class),
            any(String.class),
            any(String.class),
            any(GraphQLQueryDefinition.class),
            any(GraphQLDataFetcher.class)
        );
        verify(interceptor, times(1)).postHandle(
            any(RequestArguments.class),
            any(String.class),
            any(String.class),
            any(GraphQLQueryDefinition.class),
            any(Object.class),
            any(GraphQLDataFetcher.class)
        );
        verifyNoMoreInteractions(interceptor);

        verify(arguments, times(6)).setArgument(
            any(Object.class),
            any(Object.class)
        );
        verifyNoMoreInteractions(arguments);
    }


    @Test
    public void testFetcherNotFoundException() {
        // given
        GraphQLSchemaParser schemaParser = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder().build();
        ObjectMapper objectMapper = new ObjectMapper();

        RequestArguments arguments = mock(RequestArguments.class);

        GraphQLController controller = GraphQLController.builder()
            .schemaParser(schemaParser)
            .dataFetcherManager(dataFetcherManager)
            .objectMapper(objectMapper)
            .build();

        String heroQuery = "{hero}";

        // when
        Throwable e = Asserts.assertThrows(() -> controller.doGet(arguments, heroQuery, null));

        // then
        Asserts.assertEquals(HttpNotFoundException.class.toString(), e.getClass().toString());
    }

    @Test
    public void testInterceptorFalse() {
        // given
        GraphQLSchemaParser schemaParser = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        GraphQLDataFetcher heroDataFetcher = new GraphQLHeroDataFetcher();
        GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder()
            .addDataFetcher(heroDataFetcher)
            .build();
        ObjectMapper objectMapper = new ObjectMapper();

        RequestArguments arguments = mock(RequestArguments.class);
        GraphQLInterceptor interceptor = mock(GraphQLInterceptor.class);
        when(
            interceptor.preHandle(
                any(RequestArguments.class),
                any(String.class),
                any(String.class),
                any(GraphQLQueryDefinition.class),
                any(GraphQLDataFetcher.class)
            )
        ).thenReturn(false);

        GraphQLInterceptorManager interceptorManager = mock(GraphQLInterceptorManager.class);
        when(interceptorManager.getRequestInterceptors()).thenReturn(
            Collections.singletonList(interceptor)
        );

        GraphQLController controller = GraphQLController.builder()
            .schemaParser(schemaParser)
            .dataFetcherManager(dataFetcherManager)
            .objectMapper(objectMapper)
            .interceptorManager(interceptorManager)
            .build();

        String heroQuery = "{hero}";

        // when
        Throwable e = Asserts.assertThrows(() -> controller.doGet(arguments, heroQuery, null));

        // then
        Asserts.assertEqualsType(e, HttpNotAcceptableException.class);

        verify(interceptorManager, times(1)).getRequestInterceptors();
        verifyNoMoreInteractions(interceptorManager);

        verify(interceptor, times(1)).preHandle(
            any(RequestArguments.class),
            any(String.class),
            any(String.class),
            any(GraphQLQueryDefinition.class),
            any(GraphQLDataFetcher.class)
        );
        verifyNoMoreInteractions(interceptor);

        verify(arguments, times(3)).setArgument(
            any(Object.class),
            any(Object.class)
        );
        verifyNoMoreInteractions(arguments);
    }

    @Test
    public void testQueryWithVariables() {
        // given
        GraphQLSchemaParser schemaParser = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );
        GraphQLDataFetcher welcomeDataFetcher = new GraphQLWelcomeDataFetcher();

        GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder()
            .addDataFetcher(welcomeDataFetcher)
            .build();
        ObjectMapper objectMapper = new ObjectMapper();

        RequestArguments arguments = mock(RequestArguments.class);
        GraphQLInterceptor interceptor = mock(GraphQLInterceptor.class);
        when(
            interceptor.preHandle(
                any(RequestArguments.class),
                any(String.class),
                any(String.class),
                any(GraphQLQueryDefinition.class),
                any(GraphQLDataFetcher.class)
            )
        ).thenReturn(true);

        GraphQLInterceptorManager interceptorManager = mock(GraphQLInterceptorManager.class);
        when(interceptorManager.getRequestInterceptors()).thenReturn(
            Collections.singletonList(interceptor)
        );

        GraphQLController controller = GraphQLController.builder()
            .schemaParser(schemaParser)
            .dataFetcherManager(dataFetcherManager)
            .objectMapper(objectMapper)
            .interceptorManager(interceptorManager)
            .build();

        String welcomeQuery = "{welcome(name: $name)}";
        Map<String, Object> variables = Collections.singletonMap(
            "name", "Foo"
        );
        String variablesString = "{\"name\":\"Foo\"}";
        GraphQLRequest welcomeRequest = new GraphQLRequest();
        welcomeRequest.setQuery(welcomeQuery);
        welcomeRequest.setVariables(variables);

        // when
        Throwable e = Asserts.assertThrows(() ->
            controller.doGet(arguments, welcomeQuery, variablesString)
        );

        // then
        Asserts.assertEqualsType(e, HttpInternalServerErrorException.class);

        verify(interceptorManager, times(1)).getRequestInterceptors();
        verifyNoMoreInteractions(interceptorManager);

        verify(interceptor, times(1)).preHandle(
            any(RequestArguments.class),
            any(String.class),
            any(String.class),
            any(GraphQLQueryDefinition.class),
            any(GraphQLDataFetcher.class)
        );
        verifyNoMoreInteractions(interceptor);

        verify(arguments, times(3)).setArgument(
            any(Object.class),
            any(Object.class)
        );
        verifyNoMoreInteractions(arguments);
    }

    @Test
    public void testQueryWithNullVariableType() {
        // given
        GraphQLSchemaParser schemaParser = new GraphQLSchemaParser(
            new GraphQLObjectMapperFactory().newObjectMapper()
        );
        GraphQLDataFetcher fooDataFetcher = new GraphQLFooDataFetcher();
        GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder()
            .addDataFetcher(fooDataFetcher)
            .build();
        ObjectMapper objectMapper = new ObjectMapper();

        RequestArguments arguments = mock(RequestArguments.class);
        GraphQLInterceptor interceptor = mock(GraphQLInterceptor.class);
        when(
            interceptor.preHandle(
                any(RequestArguments.class),
                any(String.class),
                any(String.class),
                any(GraphQLQueryDefinition.class),
                any(GraphQLDataFetcher.class)
            )
        ).thenReturn(true);

        GraphQLInterceptorManager interceptorManager = mock(GraphQLInterceptorManager.class);
        when(interceptorManager.getRequestInterceptors()).thenReturn(
            Collections.singletonList(interceptor)
        );

        GraphQLController controller = GraphQLController.builder()
            .schemaParser(schemaParser)
            .dataFilter(new GraphQLDataFilter())
            .dataFetcherManager(dataFetcherManager)
            .objectMapper(objectMapper)
            .interceptorManager(interceptorManager)
            .build();

        String fooQuery = "{foo{value(value:$value){*}}}";

        // when
        Object fooResult1 = controller.doGet(arguments, fooQuery, "{\"value\": \"Bar\"}");
        Object fooResult2 = controller.doGet(arguments, fooQuery, null);

        // then
        Asserts.assertEquals(fooResult1.toString(), "{foo={value={bar=Bar}}}");
        Asserts.assertEquals(fooResult2.toString(), "{foo={value={}}}");

        verify(interceptorManager, times(2)).getRequestInterceptors();
        verifyNoMoreInteractions(interceptorManager);

        verify(interceptor, times(2)).preHandle(
            any(RequestArguments.class),
            any(String.class),
            any(String.class),
            any(GraphQLQueryDefinition.class),
            any(GraphQLDataFetcher.class)
        );
        verify(interceptor, times(2)).postHandle(
            any(RequestArguments.class),
            any(String.class),
            any(String.class),
            any(GraphQLQueryDefinition.class),
            any(Object.class),
            any(GraphQLDataFetcher.class)
        );
        verifyNoMoreInteractions(interceptor);

        verify(arguments, times(6)).setArgument(
            any(Object.class),
            any(Object.class)
        );
        verifyNoMoreInteractions(arguments);
    }

    @Test
    public void testInvalidScheme() {
        // given
        GraphQLSchemaParser schemaParser = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        GraphQLDataFetcher meDataFetcher = new GraphQLYouDataFetcher();
        GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder()
            .addDataFetcher(meDataFetcher)
            .build();
        ObjectMapper objectMapper = new ObjectMapper();

        RequestArguments arguments = mock(RequestArguments.class);
        GraphQLInterceptor interceptor = mock(GraphQLInterceptor.class);
        when(
            interceptor.preHandle(
                any(RequestArguments.class),
                any(String.class),
                any(String.class),
                any(GraphQLQueryDefinition.class),
                any(GraphQLDataFetcher.class)
            )
        ).thenReturn(true);

        GraphQLInterceptorManager interceptorManager = mock(GraphQLInterceptorManager.class);
        when(interceptorManager.getRequestInterceptors()).thenReturn(
            Collections.singletonList(interceptor)
        );

        GraphQLController controller = GraphQLController.builder()
            .schemaParser(schemaParser)
            .dataFilter(new GraphQLDataFilter())
            .dataFetcherManager(dataFetcherManager)
            .objectMapper(objectMapper)
            .interceptorManager(interceptorManager)
            .build();

        GraphQLRequest youRequest = new GraphQLRequest();
        youRequest.setQuery("query{you{friends{name}}}}");

        // when
        Throwable e = Asserts.assertThrows(() -> controller.doPost(arguments, youRequest));

        // then
        Asserts.assertEquals(GraphQLInvalidSchemeException.class.toString(), e.getClass().toString());

        verify(interceptorManager, times(1)).getRequestInterceptors();
        verifyNoMoreInteractions(interceptorManager);

        verify(interceptor, times(1)).preHandle(
            any(RequestArguments.class),
            any(String.class),
            any(String.class),
            any(GraphQLQueryDefinition.class),
            any(GraphQLDataFetcher.class)
        );
        verifyNoMoreInteractions(interceptor);

        verify(arguments, times(3)).setArgument(
            any(Object.class),
            any(Object.class)
        );
        verifyNoMoreInteractions(arguments);
    }

    @Test
    public void testNoNameDataFetcher() {
        // given
        GraphQLDataFetcher nonameDataFetcher = new GraphQLNoNameDataFetcher();

        // when
        Throwable e = Asserts.assertThrows(() -> GraphQLDataFetcherManager.builder().addDataFetcher(nonameDataFetcher).build());

        // then
        Asserts.assertEquals(EzyNotImplementedException.class.toString(), e.getClass().toString());
    }

    @Test
    public void doGetTestException() {
        // given
        GraphQLSchemaParser schemaParser = new GraphQLSchemaParser(
            new ObjectMapper()
        );
        GraphQLDataFetcherManager dataFetcherManager = GraphQLDataFetcherManager.builder().build();
        ObjectMapper objectMapper = new ObjectMapper();

        RequestArguments arguments = mock(RequestArguments.class);

        GraphQLController controller = GraphQLController.builder()
            .schemaParser(schemaParser)
            .dataFetcherManager(dataFetcherManager)
            .objectMapper(objectMapper)
            .build();

        String heroQuery = "{hero}";

        // when
        Throwable e = Asserts.assertThrows(() -> controller.doGet(arguments, heroQuery, "abc"));

        // then
        Asserts.assertEqualsType(e, HttpBadRequestException.class);
    }
}
