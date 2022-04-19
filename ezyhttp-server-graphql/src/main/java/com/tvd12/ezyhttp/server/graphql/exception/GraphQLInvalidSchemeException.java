package com.tvd12.ezyhttp.server.graphql.exception;

public class GraphQLInvalidSchemeException extends IllegalArgumentException {
    private static final long serialVersionUID = 1908055626427476066L;

    public GraphQLInvalidSchemeException(String s) {
        super(s);
    }
}
