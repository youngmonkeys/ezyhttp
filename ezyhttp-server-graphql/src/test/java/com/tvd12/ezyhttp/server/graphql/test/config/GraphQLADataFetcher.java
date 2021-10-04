package com.tvd12.ezyhttp.server.graphql.test.config;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.graphql.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.annotation.GraphQLQuery;

@EzySingleton
@GraphQLQuery(name = "A")
public class GraphQLADataFetcher implements GraphQLDataFetcher {
	@Override
	public Object getData(Object argument) {
		return "A";
	}
}
