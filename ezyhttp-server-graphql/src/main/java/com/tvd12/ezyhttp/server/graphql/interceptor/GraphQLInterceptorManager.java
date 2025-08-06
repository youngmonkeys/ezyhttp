package com.tvd12.ezyhttp.server.graphql.interceptor;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

@Getter
public class GraphQLInterceptorManager {

    protected final List<GraphQLInterceptor> requestInterceptors;

    @SuppressWarnings("unchecked")
    public GraphQLInterceptorManager(
        EzySingletonFactory singletonFactory
    ) {
        this.requestInterceptors = singletonFactory.getSingletonsOf(
            GraphQLInterceptor.class
        );
        this.requestInterceptors.sort(
            Comparator.comparingInt(GraphQLInterceptor::getPriority)
        );
    }
}
