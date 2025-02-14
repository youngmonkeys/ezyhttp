package com.tvd12.ezyhttp.core.util;

import java.util.Locale;

public final class Locales {

    private Locales() {}

    public static Locale getLocaleFromLanguage(String lang) {
        Locale locale;
        if (lang.contains("_")) {
            int index = lang.indexOf('_');
            String language = lang.substring(0, index);
            String country = lang.substring(index + 1);
            locale = new Locale(language, country);
        } else if (lang.contains("-")) {
            int index = lang.indexOf('-');
            String language = lang.substring(0, index);
            String country = lang.substring(index + 1);
            locale = new Locale(language, country);
        } else {
            locale = new Locale(lang);
        }
        return locale;
    }
}
