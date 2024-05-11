package com.tvd12.ezyhttp.server.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DoGet {

    /**
     * request uri.
     *
     * @return the uri
     */
    String value() default "";

    /**
     * request uri.
     *
     * @return the uri
     */
    String uri() default "";

    /**
     * other request uris.
     *
     * @return the other request uris
     */
    String[] otherUris() default "";

    /**
     * accepted request body types.
     *
     * @return the accepted request body types
     */
    String[] accept() default {};

    /**
     * request uri.
     *
     * @return the response body type
     */
    String responseType() default "";
}
