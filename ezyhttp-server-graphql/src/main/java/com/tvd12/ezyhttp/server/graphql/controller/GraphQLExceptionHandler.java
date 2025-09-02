package com.tvd12.ezyhttp.server.graphql.controller;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.server.core.annotation.ExceptionHandler;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLFetcherException;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLInvalidSchemeException;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLObjectMapperException;

@ExceptionHandler
public class GraphQLExceptionHandler extends EzyLoggable {

    @TryCatch(GraphQLFetcherException.class)
    public Object handle(GraphQLFetcherException e) {
        logger.info("{}({})", e.getClass().getSimpleName(), e.getMessage());
        return e.toDataMap();
    }

    @TryCatch(GraphQLInvalidSchemeException.class)
    public Object handle(GraphQLInvalidSchemeException e) {
        logger.info("{}({})", e.getClass().getSimpleName(), e.getMessage());
        return e.getErrors();
    }

    @TryCatch(GraphQLObjectMapperException.class)
    public Object handle(GraphQLObjectMapperException e) {
        logger.info("{}({})", e.getClass().getSimpleName(), e.getMessage());
        return e.getErrors();
    }
}
