package com.tvd12.ezyhttp.server.graphql.test.interceptor;

import com.tvd12.ezyhttp.server.graphql.interceptor.GraphQLInterceptor;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class GraphQLInterceptorTest {

    @Test
    public void test() {
        // given
        ExGraphQLInterceptor interceptor = new ExGraphQLInterceptor();

        // when
        boolean ok = interceptor.preHandle(
            null,
            null,
            null,
            null,
            null
        );
        interceptor.postHandle(
            null,
            null,
            null,
            null,
            null
        );
        int priority = interceptor.getPriority();

        // then
        Asserts.assertTrue(ok);
        Asserts.assertZero(priority);
    }

    public static class ExGraphQLInterceptor implements GraphQLInterceptor {}
}
