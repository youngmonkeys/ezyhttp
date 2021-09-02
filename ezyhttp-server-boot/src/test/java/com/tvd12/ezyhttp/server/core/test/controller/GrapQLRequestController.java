package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;
import com.tvd12.ezyhttp.server.core.graphql.GraphQLProxy;
import com.tvd12.ezyhttp.server.core.graphql.GraphQLRequest;

import lombok.Builder;
import lombok.Data;

@Controller("/graphql")
public class GrapQLRequestController {

	@DoGet("/get")
	public Object graphqlGet(@RequestParam("query") String query) {
		GraphQLRequest document = GraphQLProxy.getInstance().parseQuery(query);
		return HelloResponse.builder()
				.message("Hello")
				.build();
	}
	
	
	@Data
	public static class HelloRequest {
		private String who;
	}
	
	@Data
	@Builder
	public static class HelloResponse {
		private String message;
	}
}
