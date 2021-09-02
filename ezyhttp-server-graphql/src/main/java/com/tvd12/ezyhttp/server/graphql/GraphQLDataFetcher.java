package com.tvd12.ezyhttp.server.graphql;

import com.tvd12.ezyfox.reflect.EzyGenerics;

public interface GraphQLDataFetcher<A,D> {

	D getData(A argument);
	
	
	default Class<?> getArgumentType() {
		try {
			Class<?> readerClass = getClass();
			Class<?>[] args = EzyGenerics.getGenericInterfacesArguments(readerClass, GraphQLDataFetcher.class, 2);
			return args[0];
		}
		catch(Exception e) {
			return null;
		}
	}
}
