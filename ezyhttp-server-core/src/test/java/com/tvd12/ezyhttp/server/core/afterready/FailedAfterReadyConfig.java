package com.tvd12.ezyhttp.server.core.afterready;

import com.tvd12.ezyfox.bean.EzyBeanConfig;
import com.tvd12.ezyhttp.server.core.annotation.EzyConfigurationAfterApplicationReady;

@EzyConfigurationAfterApplicationReady(priority = -100)
public class FailedAfterReadyConfig implements EzyBeanConfig {

    @Override
    public void config() {
        throw new IllegalStateException("test");
    }
}
