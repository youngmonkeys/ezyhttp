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

    default void render(
        HttpServletRequest request,
        HttpServletResponse response,
        View view
    ) throws IOException {
        render(null, request, response, view);
    }

    void render(
        ServletContext servletContext,
        HttpServletRequest request,
        HttpServletResponse response,
        View view
    ) throws IOException;

    default String renderHtml(View view) {
        throw new UnsupportedOperationException("not implemented");
    }

    default String renderHtml(Object context, View view) {
        return renderHtml(view);
    }

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

    default I18nMessageResolver getMessageResolver() {
        throw new UnsupportedOperationException("not implemented");
    }

    default <T> T getTemplateEngine() {
        return null;
    }

    default <T> T getContentTemplateEngine() {
        return null;
    }
}
