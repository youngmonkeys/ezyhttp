package com.tvd12.ezyhttp.core.boot.test.graphql;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.graphql.GraphQLAbstractDataFetcher;
import com.tvd12.ezyhttp.server.graphql.annotation.GraphQLQuery;

@EzySingleton
public class GreetingSingleton {
	
	@GraphQLQuery(name = "greeting")
	public String greeting() {
		return "Aloha";
	}
	
}
