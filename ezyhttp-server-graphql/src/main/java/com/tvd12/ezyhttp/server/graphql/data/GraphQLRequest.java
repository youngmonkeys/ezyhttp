package com.tvd12.ezyhttp.server.graphql.data;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Map;

@Getter
@Setter
public class GraphQLRequest {
    private String query;
    private Map<String, Object> variables;

    public Map<String, Object> getVariables() {
        return variables != null
            ? variables
            : Collections.emptyMap();
    }
}
