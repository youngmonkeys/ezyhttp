package com.tvd12.ezyhttp.server.jetty.test.annotation;

import java.lang.annotation.*;

/**
 * @author tavandung12
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface NickName { }
