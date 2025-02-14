package com.tvd12.ezyhttp.server.core.test.config;

import com.tvd12.ezyfox.bean.EzyBeanConfig;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.core.annotation.EzyConfigurationAfterApplicationReady;
import com.tvd12.ezyhttp.server.core.view.I18nMessageResolver;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@EzyConfigurationAfterApplicationReady(priority = Integer.MIN_VALUE)
public class ServerReadyConfig3 implements EzyBeanConfig {

    private final EzySingletonFactory singletonFactory;

    @Override
    public void config() {
        ViewContext viewContext = singletonFactory
            .getSingletonCast(ViewContext.class);
        I18nMessageResolver messageResolver = viewContext
            .getMessageResolver();
        messageResolver.putI18nMessages(
            EzyMapBuilder.mapBuilder()
                .put("hello", "world")
                .toMap()
        );
    }
}
