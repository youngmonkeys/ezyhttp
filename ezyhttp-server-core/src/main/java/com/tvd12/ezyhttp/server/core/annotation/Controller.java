package com.tvd12.ezyhttp.server.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Controller {

    /**
     * the request uri.
     *
     * @return the uri
     */
    String value() default "";

    /**
     * the request uri.
     *
     * @return the uri
     */
    String uri() default "";
}
