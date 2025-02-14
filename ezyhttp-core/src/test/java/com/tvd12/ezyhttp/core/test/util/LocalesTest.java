package com.tvd12.ezyhttp.core.test.util;

import com.tvd12.ezyhttp.core.util.Locales;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Locale;

public class LocalesTest {

    @Test
    public void test() {
        // given
        // when
        // then
        Asserts.assertEquals(
            Locales.getLocaleFromLanguage("vi"),
            new Locale("vi")
        );
        Asserts.assertEquals(
            Locales.getLocaleFromLanguage("vi_VN"),
            new Locale("vi", "VN")
        );
        Asserts.assertEquals(
            Locales.getLocaleFromLanguage("vi-VN"),
            new Locale("vi", "VN")
        );
    }
}
