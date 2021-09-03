package com.tvd12.ezyhttp.server.core.annotation;

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
@Target({ ElementType.PARAMETER })
public @interface RequestHeader {
    
	/**
	 * name of header
	 * 
	 * @return the header's name
	 */
	public String value() default "";
}
