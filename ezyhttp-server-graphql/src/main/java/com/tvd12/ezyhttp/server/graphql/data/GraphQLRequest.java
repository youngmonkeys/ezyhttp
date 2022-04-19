package com.tvd12.ezyhttp.server.graphql.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GraphQLRequest {

    private String query;
    private Object variables;
}
