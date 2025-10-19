package com.tvd12.ezyhttp.server.core.test.config;

import com.tvd12.ezyfox.bean.EzyBeanConfig;
import com.tvd12.ezyfox.bean.EzyBeanContextBuilder;
import com.tvd12.ezyfox.bean.EzyBeanContextBuilderAware;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationBefore;
import com.tvd12.ezyhttp.server.core.test.controller.NoAnnotationController;
import lombok.Setter;

@Setter
@EzyConfigurationBefore
public class ConfigurationBefore implements
    EzyBeanContextBuilderAware,
    EzyBeanConfig {

    private EzyBeanContextBuilder contextBuilder;

    @Override
    public void config() {
        contextBuilder.addSingletonClasses(
            NoAnnotationController.class
        );
    }
}
