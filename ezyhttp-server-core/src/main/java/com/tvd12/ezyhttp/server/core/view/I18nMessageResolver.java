package com.tvd12.ezyhttp.server.core.view;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public interface I18nMessageResolver {

    default void putI18nMessages(
        Map<String, Map<String, String>> newMessagesByLanguage
    ) {}

    default Map<String, Map<String, String>> putI18nMessagesIfAbsent(
        Map<String, Map<String, String>> newMessagesByLanguage
    ) {
        throw new UnsupportedOperationException();
    }

    default void removeI18nMessages(
        Map<String, Map<String, String>> messagesByLanguage
    ) {
        throw new UnsupportedOperationException();
    }

    default Set<String> getMessageLanguages() {
        throw new UnsupportedOperationException();
    }

    default Set<Locale> getMessageLocales() {
        throw new UnsupportedOperationException();
    }

    default Properties getDefaultMessages() {
        throw new UnsupportedOperationException();
    }

    default Map<Locale, Properties> getMessagesByLocale() {
        throw new UnsupportedOperationException();
    }

    default Properties getMessagesByLocale(
        Locale locale
    ) {
        throw new UnsupportedOperationException();
    }

    default Map<String, Properties> getMessagesByLanguage() {
        throw new UnsupportedOperationException();
    }

    default Properties getMessagesByLanguage(
        String language
    ) {
        throw new UnsupportedOperationException();
    }

    default Set<String> getKeysOfMessageContainsKeyword(
        String keyword
    ) {
        throw new UnsupportedOperationException();
    }
}
