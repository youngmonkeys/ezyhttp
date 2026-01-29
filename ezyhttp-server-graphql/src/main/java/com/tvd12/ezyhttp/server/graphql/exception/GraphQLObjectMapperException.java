package com.tvd12.ezyhttp.server.graphql.exception;

import lombok.Getter;

@Getter
public class GraphQLObjectMapperException extends IllegalArgumentException {
    private static final long serialVersionUID = 3508582611517214186L;

    private final Object errors;

    public GraphQLObjectMapperException(
        Object errors
    ) {
        super(errors.toString());
        this.errors = errors;
    }

    public GraphQLObjectMapperException(
        Object errors,
        Throwable cause
    ) {
        this(errors, errors.toString(), cause);
    }

    public GraphQLObjectMapperException(
        Object errors,
        String message,
        Throwable cause
    ) {
        super(message, cause);
        this.errors = errors;
    }
}
