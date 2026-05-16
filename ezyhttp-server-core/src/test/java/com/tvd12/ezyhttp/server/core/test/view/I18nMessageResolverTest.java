package com.tvd12.ezyhttp.server.core.test.view;

import com.tvd12.ezyhttp.server.core.view.I18nMessageResolver;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Locale;

public class I18nMessageResolverTest {

    @Test
    public void test() {
        // given
        I18nMessageResolver instance = new I18nMessageResolver() {};

        // when
        // then
        instance.putI18nMessages(Collections.emptyMap());
        Asserts.assertEqualsType(
            Asserts.assertThrows(instance::getMessageLanguages),
            UnsupportedOperationException.class
        );
        Asserts.assertEqualsType(
            Asserts.assertThrows(instance::getMessageLocales),
            UnsupportedOperationException.class
        );
        Asserts.assertEqualsType(
            Asserts.assertThrows(instance::getDefaultMessages),
            UnsupportedOperationException.class
        );
        Asserts.assertEqualsType(
            Asserts.assertThrows(instance::getMessagesByLocale),
            UnsupportedOperationException.class
        );
        Asserts.assertEqualsType(
            Asserts.assertThrows(() ->
                instance.getMessagesByLocale(Locale.CANADA)
            ),
            UnsupportedOperationException.class
        );
        Asserts.assertEqualsType(
            Asserts.assertThrows(instance::getMessagesByLanguage),
            UnsupportedOperationException.class
        );
        Asserts.assertEqualsType(
            Asserts.assertThrows(() ->
                instance.getMessagesByLanguage("a")
            ),
            UnsupportedOperationException.class
        );
        Asserts.assertEqualsType(
            Asserts.assertThrows(() ->
                instance.getKeysOfMessageContainsKeyword("")
            ),
            UnsupportedOperationException.class
        );
    }
}
