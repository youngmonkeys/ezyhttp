package com.tvd12.ezyhttp.server.graphql.exception;

import lombok.Getter;

@Getter
public class GraphQLInvalidSchemeException extends IllegalArgumentException {
    private static final long serialVersionUID = 1908055626427476066L;

    private final Object errors;

    public GraphQLInvalidSchemeException(
        Object errors
    ) {
        this(errors, errors.toString());
    }

    public GraphQLInvalidSchemeException(
        Object errors,
        String message
    ) {
        super(message);
        this.errors = errors;
    }
}
