package com.tvd12.ezyhttp.server.core.afterready;

import com.tvd12.ezyfox.bean.EzyBeanConfig;
import com.tvd12.ezyhttp.server.core.annotation.EzyConfigurationAfterApplicationReady;
import lombok.Getter;

@Getter
@EzyConfigurationAfterApplicationReady(priority = 100)
public class SucceededAfterReadyConfig implements EzyBeanConfig {

    private boolean configured;

    @Override
    public void config() {
        configured = true;
    }
}
