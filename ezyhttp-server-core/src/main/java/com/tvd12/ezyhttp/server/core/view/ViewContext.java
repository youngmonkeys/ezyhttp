package com.tvd12.ezyhttp.server.core.view;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public interface ViewContext {

    void render(
        ServletContext servletContext,
        HttpServletRequest request,
        HttpServletResponse response,
        View view
    ) throws IOException;

    String resolveMessage(
        Locale locale,
        String key,
        Object... parameters
    );

    default Map<String, String> resolveMessages(
        Locale locale,
        Collection<String> keys
    ) {
        return keys
            .stream()
            .collect(
                Collectors.toMap(
                    it -> it,
                    it -> resolveMessage(locale, it),
                    (o, n) -> n
                )
            );
    }

    default I18nMessageResolver getI18nMessageResolver() {
        throw new UnsupportedOperationException("not implemented");
    }
}
