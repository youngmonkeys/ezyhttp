package com.tvd12.ezyhttp.server.core.test.view;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyhttp.server.core.view.*;
import org.testng.annotations.Test;

import com.tvd12.test.assertion.Asserts;

import lombok.AllArgsConstructor;

public class ViewContextBuilderTest {

    @Test
    public void addViewDecoratorsTest() {
        // given
        InternalViewContext context = (InternalViewContext) new InternalViewContextBuilder()
            .viewDecorators(
                Arrays.asList(
                    new ViewDecorator1(),
                    new ViewDecorator2()
                )
            )
            .build();
        // when
        // then
        Asserts.assertEquals(
            context.viewDecorators,
            Arrays.asList(
                new ViewDecorator2(),
                new ViewDecorator1()
            ),
            false
        );
    }

    @AllArgsConstructor
    private static class InternalViewContext implements ViewContext {

        private final List<ViewDecorator> viewDecorators;

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
            return key;
        }

        @Override
        public I18nMessageResolver getMessageResolver() {
            return new I18nMessageResolver() {};
        }
    }

    private static class InternalViewContextBuilder extends ViewContextBuilder {

        @Override
        public InternalViewContext build() {
            return new InternalViewContext(viewDecorators);
        }

    }

    private static class ViewDecorator1 implements ViewDecorator {
        @Override
        public void decorate(HttpServletRequest request, View view) {}
    }

    private static class ViewDecorator2 implements ViewDecorator {
        @Override
        public void decorate(HttpServletRequest request, View view) {}

        @Override
        public int getPriority() {
            return -1;
        }
    }
}
