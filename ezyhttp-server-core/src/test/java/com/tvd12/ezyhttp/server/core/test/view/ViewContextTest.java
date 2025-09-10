package com.tvd12.ezyhttp.server.core.test.view;

import com.tvd12.ezyfox.collect.Lists;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class ViewContextTest {

    @Test
    public void test() throws Exception {
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

        List<String> keys = Lists.newArrayList("hello", "world", "hello");

        // when
        Map<String, String> actual = sut.resolveMessages(
            Locale.US,
            keys
        );
        sut.render(
            mock(HttpServletRequest.class),
            mock(HttpServletResponse.class),
            View.builder()
                .template("test")
                .build()
        );
        Throwable e1 = Asserts.assertThrows(sut::getMessageResolver);
        Throwable e2 = Asserts.assertThrows(() ->
            sut.renderHtml(
                new Object(),
                View.builder()
                    .template("test")
                    .build()
            )
        );
        Object templateEngine = sut.getTemplateEngine();
        Object contentTemplateEngine = sut.getContentTemplateEngine();

        // then
        Asserts.assertEquals(
            actual,
            EzyMapBuilder.mapBuilder()
                .put("hello", "HELLO")
                .put("world", "WORLD")
                .build()
        );
        Asserts.assertEqualsType(e1, UnsupportedOperationException.class);
        Asserts.assertEqualsType(e2, UnsupportedOperationException.class);
        Asserts.assertNull(templateEngine);
        Asserts.assertNull(contentTemplateEngine);
    }

    @Test
    public void renderHtmlTest() {
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
            public String renderHtml(View view) {
                return  null;
            }

            @Override
            public String resolveMessage(
                Locale locale,
                String key,
                Object... parameters
            ) {
                return key.toUpperCase();
            }
        };

        // when
        String actual = sut.renderHtml(
            new Object(),
            View.builder()
                .template("test")
                .build()
        );

        // then
        Asserts.assertNull(actual);
    }
}
