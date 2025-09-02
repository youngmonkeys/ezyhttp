package com.tvd12.ezyhttp.server.graphql.data;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class GraphQLError {
    private String message;
    private List<Map<String, Object>> locations;
    private List<String> path;
    private Map<String, Object> extensions;
}
