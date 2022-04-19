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
public @interface RequestParam {
    
    /**
     * name or parameter
     * 
     * @return the parameter's name
     */
    String value() default "";
    
    /**
     * name or parameter
     * 
     * @return the parameter's name
     */
    String name() default "";
    
    /**
     * default or parameter
     * 
     * @return the default value of the parameter
     */
    String defaultValue() default "null";
}
