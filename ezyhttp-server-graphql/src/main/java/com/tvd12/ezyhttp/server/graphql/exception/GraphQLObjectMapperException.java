package com.tvd12.ezyhttp.server.graphql.exception;

import com.tvd12.ezyhttp.server.graphql.data.GraphQLError;
import lombok.Getter;

import java.util.List;

@Getter
public class GraphQLObjectMapperException extends IllegalArgumentException {
    private static final long serialVersionUID = 3508582611517214186L;

    private final List<GraphQLError> errors;

    public GraphQLObjectMapperException(
        List<GraphQLError> errors
    ) {
        super(errors.toString());
        this.errors = errors;
    }

    public GraphQLObjectMapperException(
        List<GraphQLError> errors,
        Throwable cause
    ) {
        this(errors, errors.toString(), cause);
    }

    public GraphQLObjectMapperException(
        List<GraphQLError> errors,
        String message,
        Throwable cause
    ) {
        super(message, cause);
        this.errors = errors;
    }
}
