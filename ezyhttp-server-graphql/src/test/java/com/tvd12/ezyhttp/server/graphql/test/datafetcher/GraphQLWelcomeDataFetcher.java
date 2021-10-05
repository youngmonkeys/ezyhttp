package com.tvd12.ezyhttp.server.graphql.test.datafetcher;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.graphql.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.test.datafetcher.GraphQLWelcomeDataFetcher.WelcomeRequest;
import lombok.Data;


@EzySingleton
public class GraphQLWelcomeDataFetcher implements GraphQLDataFetcher<WelcomeRequest, String> {
	
	public String getData(WelcomeRequest argument) {
		return "Welcome " + argument.getName();
	}
	
	@Override
	public String getQueryName() {
		return "welcome";
	}
	
	@Data
	public static class WelcomeRequest {
		private String name;
	}
}
