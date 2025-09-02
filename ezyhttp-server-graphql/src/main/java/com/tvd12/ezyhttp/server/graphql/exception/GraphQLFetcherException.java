package com.tvd12.ezyhttp.server.graphql.exception;

import com.tvd12.ezyhttp.server.graphql.data.GraphQLError;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GraphQLFetcherException extends RuntimeException {

    private final Map<String, Object> data;
    private final List<GraphQLError> errors;

    public GraphQLFetcherException(
        List<GraphQLError> errors
    ) {
        this(null, errors);
    }

    public GraphQLFetcherException(
        Map<String, Object> data,
        List<GraphQLError> errors
    ) {
        super("data: " + data + ", errors: " + errors);
        this.data = data;
        this.errors = errors;
    }

    public Map<String, Object> toDataMap() {
        Map<String, Object> answer = new HashMap<>();
        if (data != null) {
            answer.put("data", data);
        }
        answer.put("errors", errors);
        return answer;
    }
}
