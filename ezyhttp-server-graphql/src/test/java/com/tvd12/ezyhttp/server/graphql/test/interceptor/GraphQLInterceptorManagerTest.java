package com.tvd12.ezyhttp.server.graphql.test.interceptor;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyhttp.server.graphql.interceptor.GraphQLInterceptor;
import com.tvd12.ezyhttp.server.graphql.interceptor.GraphQLInterceptorManager;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class GraphQLInterceptorManagerTest {

    @Test
    public void test() {
        // given
        GraphQLInterceptor interceptor = mock(GraphQLInterceptor.class);

        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        when(singletonFactory.getSingletonsOf(GraphQLInterceptor.class))
            .thenReturn(Collections.singletonList(interceptor));

        // when
        GraphQLInterceptorManager manager = new GraphQLInterceptorManager(
            singletonFactory
        );

        // then
        List<GraphQLInterceptor> interceptors = manager.getRequestInterceptors();
        Asserts.assertEquals(interceptors, Collections.singletonList(interceptor), false);

        verifyNoMoreInteractions(interceptor);

        verify(singletonFactory, times(1))
            .getSingletonsOf(GraphQLInterceptor.class);
        verifyNoMoreInteractions(singletonFactory);
    }
}
