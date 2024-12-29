package com.tvd12.ezyhttp.server.core.test.config;

import com.tvd12.ezyfox.bean.EzyBeanConfig;
import com.tvd12.ezyhttp.server.core.annotation.EzyConfigurationAfterApplicationReady;

@EzyConfigurationAfterApplicationReady(priority = Integer.MAX_VALUE)
public class ServerReadyConfig2 implements EzyBeanConfig {

    @Override
    public void config() {}
}
