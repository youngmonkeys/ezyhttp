package com.tvd12.ezyhttp.server.graphql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author tavandung12
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface GraphQLOperation {
    
	/**
	 * the operation name
	 * 
	 * @return the operation name
	 */
	public String value();
}
