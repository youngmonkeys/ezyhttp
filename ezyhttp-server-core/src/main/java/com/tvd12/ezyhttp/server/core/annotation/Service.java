package com.tvd12.ezyhttp.server.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Service {

    /**
     * the service name.
     *
     * @return the service name
     */
    String value() default "";

    /**
     * the service name.
     *
     * @return the service name
     */
    String name() default "";
}
