package com.tvd12.ezyhttp.server.graphql.exception;

import com.tvd12.ezyhttp.server.graphql.data.GraphQLError;
import lombok.Getter;

import java.util.List;

@Getter
public class GraphQLInvalidSchemeException extends IllegalArgumentException {
    private static final long serialVersionUID = 1908055626427476066L;

    private final List<GraphQLError> errors;

    public GraphQLInvalidSchemeException(
        List<GraphQLError> errors
    ) {
        this(errors, errors.toString());
    }

    public GraphQLInvalidSchemeException(
        List<GraphQLError> errors,
        String message
    ) {
        super(message);
        this.errors = errors;
    }
}
