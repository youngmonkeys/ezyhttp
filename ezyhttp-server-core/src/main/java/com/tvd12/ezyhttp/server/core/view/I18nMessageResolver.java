package com.tvd12.ezyhttp.server.core.view;

import java.util.Map;

public interface I18nMessageResolver {

    default void putI18nMessages(
        Map<String, Map<String, String>> newMessagesByLanguage
    ) {}
}
