package com.tvd12.ezyhttp.server.graphql;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GraphQLRequest {

	private String operationName;
	private String query;
	private String variables;
}
