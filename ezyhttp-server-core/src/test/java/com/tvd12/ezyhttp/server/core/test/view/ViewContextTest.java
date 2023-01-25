package com.tvd12.ezyhttp.server.core.test.view;

import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ViewContextTest {

    @Test
    public void test() {
        // given
        ViewContext sut = new ViewContext() {
            @Override
            public void render(
                ServletContext servletContext,
                HttpServletRequest request,
                HttpServletResponse response,
                View view
            ) {}

            @Override
            public String resolveMessage(
                Locale locale,
                String key,
                Object... parameters
            ) {
                return key.toUpperCase();
            }
        };

        Set<String> keys = Sets.newHashSet("hello", "world");

        // when
        Map<String, String> actual = sut.resolveMessages(
            Locale.US,
            keys
        );

        // then
        Asserts.assertEquals(
            actual,
            EzyMapBuilder.mapBuilder()
                .put("hello", "HELLO")
                .put("world", "WORLD")
                .build()
        );
    }
}
