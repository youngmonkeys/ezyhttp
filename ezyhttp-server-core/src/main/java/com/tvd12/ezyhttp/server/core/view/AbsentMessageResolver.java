package com.tvd12.ezyhttp.server.core.view;

import java.util.Locale;

public interface AbsentMessageResolver {

    String resolve(
        Locale locale,
        Class<?> origin,
        String key,
        Object[] parameters
    );
}
