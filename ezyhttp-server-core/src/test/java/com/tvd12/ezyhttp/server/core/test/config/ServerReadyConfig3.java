package com.tvd12.ezyhttp.server.core.test.config;

import com.tvd12.ezyfox.bean.EzyBeanConfig;
import com.tvd12.ezyhttp.server.core.annotation.EzyConfigurationAfterApplicationReady;

@EzyConfigurationAfterApplicationReady(priority = Integer.MIN_VALUE)
public class ServerReadyConfig3 implements EzyBeanConfig {

    @Override
    public void config() {}
}
