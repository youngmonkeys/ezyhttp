package com.tvd12.ezyhttp.server.graphql.controller;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.server.core.annotation.ExceptionHandler;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLFetcherException;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLInvalidSchemeException;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLObjectMapperException;

import java.util.HashMap;
import java.util.Map;

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
        return toErrorResponse(e.getErrors());
    }

    @TryCatch(GraphQLObjectMapperException.class)
    public Object handle(GraphQLObjectMapperException e) {
        logger.info("{}({})", e.getClass().getSimpleName(), e.getMessage());
        return toErrorResponse(e.getErrors());
    }

    private Map<String, Object> toErrorResponse(Object errors) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", null);
        response.put("errors", errors);
        return response;
    }
}
