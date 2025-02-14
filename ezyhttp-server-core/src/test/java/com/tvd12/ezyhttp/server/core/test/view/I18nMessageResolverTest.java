package com.tvd12.ezyhttp.server.core.test.view;

import com.tvd12.ezyhttp.server.core.view.I18nMessageResolver;
import org.testng.annotations.Test;

import java.util.Collections;

public class I18nMessageResolverTest {

    @Test
    public void test() {
        // given
        I18nMessageResolver instance = new I18nMessageResolver() {};

        // when
        // then
        instance.putI18nMessages(Collections.emptyMap());
    }
}
