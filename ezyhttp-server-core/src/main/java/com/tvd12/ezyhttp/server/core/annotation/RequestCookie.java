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
public @interface RequestCookie {
    
	/**
	 * name or parameter
	 * 
	 * @return the parameter's name
	 */
	public String value() default "";
	
	/**
     * name or cookie
     * 
     * @return the cookie's name
     */
    public String name() default "";
    
    /**
     * default or cookie
     * 
     * @return the default value of the cookie
     */
    public String defaultValue() default "null";
}
