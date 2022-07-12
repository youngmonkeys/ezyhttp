package com.tvd12.ezyhttp.server.tomcat.test.annotation;

import java.lang.annotation.*;

/**
 * @author tavandung12
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface NickName { }
