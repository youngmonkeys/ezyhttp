package com.tvd12.ezyhttp.server.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Indicates that a class listener a request from client and process that request
 * 
 * @author tavandung12
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface RequestParam {
    
	/**
	 * Command of request
	 * 
	 * @return the command
	 */
	public String value() default "";
}
