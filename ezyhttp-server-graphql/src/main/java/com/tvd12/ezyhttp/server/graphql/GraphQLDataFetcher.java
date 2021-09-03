package com.tvd12.ezyhttp.server.graphql;

import com.tvd12.ezyfox.exception.EzyNotImplementedException;
import com.tvd12.ezyfox.reflect.EzyGenerics;
import com.tvd12.ezyhttp.server.graphql.annotation.GraphQLOperation;

public interface GraphQLDataFetcher<A,D> {

	D getData(A argument);

	default String getOperationName() {
		if(getClass().isAnnotationPresent(GraphQLOperation.class)) {
			return getClass().getAnnotation(GraphQLOperation.class).value();
		}
		throw new EzyNotImplementedException("you must implement " + 
				getClass().getName() + 
				".getOperationName() method or annotated the class with @GraphQLOperation"
		);
	}
	
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
