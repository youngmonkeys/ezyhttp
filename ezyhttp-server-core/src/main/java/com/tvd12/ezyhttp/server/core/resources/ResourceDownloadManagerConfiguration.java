package com.tvd12.ezyhttp.server.core.resources;

import static com.tvd12.ezyhttp.server.core.resources.ResourceResolvers.createDownloadManager;

import com.tvd12.ezyfox.annotation.EzyProperty;
import com.tvd12.ezyfox.bean.EzyBeanConfig;
import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.bean.EzyBeanContextAware;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationBefore;
import com.tvd12.ezyhttp.server.core.constant.PropertyNames;

import lombok.Setter;

@Setter
@EzyConfigurationBefore
public class ResourceDownloadManagerConfiguration implements
    EzyBeanContextAware,
    EzyBeanConfig {

    @EzyProperty(PropertyNames.RESOURCE_ENABLE)
    private boolean resourcesEnable = false;

    private EzyBeanContext context;

    @Override
    public void config() {
        if (resourcesEnable) {
            context
                .getSingletonFactory()
                .addSingleton(createDownloadManager(context));
        }
    }
}
