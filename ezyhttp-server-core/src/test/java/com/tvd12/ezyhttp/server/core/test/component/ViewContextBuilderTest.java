package com.tvd12.ezyhttp.server.core.test.component;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.view.I18nMessageResolver;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import com.tvd12.ezyhttp.server.core.view.ViewContextBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@EzySingleton
public class ViewContextBuilderTest extends ViewContextBuilder {

    @Override
    public ViewContext build() {
        return new ViewContextTest();
    }

    public static class ViewContextTest implements ViewContext {

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
}
