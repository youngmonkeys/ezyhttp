package com.tvd12.ezyhttp.server.graphql;

import java.lang.reflect.Type;

import com.tvd12.ezyfox.reflect.EzyGenerics;
import com.tvd12.ezyfox.util.EzyLoggable;

public abstract class GraphQLAbstractDataFetcher<A, D>
    extends EzyLoggable
    implements GraphQLDataFetcher<A, D> {

    @Override
    public Class<?> getArgumentType() {
        try {
            Type genericSuperclass = getClass().getGenericSuperclass();
            Class<?>[] args = EzyGenerics.getTwoGenericClassArguments(genericSuperclass);
            return args[0];
        } catch (Exception e) {
            return null;
        }
    }
}
