package com.tvd12.ezyhttp.server.core.view;

import java.util.Map;
import java.util.Properties;

public interface MessageProvider {

    /**
     * Provide messages maps to language.
     *
     * @return the messages that maps to language
     */
    Map<String, Properties> provide();
}
