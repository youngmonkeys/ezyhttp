package com.tvd12.ezyhttp.server.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author tavandung12
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface DoPost {
    
	/**
	 * request uri
	 * 
	 * @return the uri
	 */
	public String value() default "";
	
	/**
	 * request uri
	 * 
	 * @return the uri
	 */
	public String uri() default "";
	
	/**
	 * request uri
	 * 
	 * @return the response body type
	 */
	public String responseType() default "";
}
