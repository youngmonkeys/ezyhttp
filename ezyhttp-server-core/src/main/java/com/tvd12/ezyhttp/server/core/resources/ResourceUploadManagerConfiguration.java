package com.tvd12.ezyhttp.server.core.resources;

import static com.tvd12.ezyhttp.server.core.resources.ResourceResolvers.createUploadManager;

import com.tvd12.ezyfox.annotation.EzyProperty;
import com.tvd12.ezyfox.bean.EzyBeanConfig;
import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.bean.EzyBeanContextAware;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationBefore;
import com.tvd12.ezyhttp.core.resources.ResourceUploadManager;
import com.tvd12.ezyhttp.server.core.constant.PropertyNames;

import lombok.Setter;

@Setter
@EzyConfigurationBefore
public class ResourceUploadManagerConfiguration 
        implements EzyBeanContextAware, EzyBeanConfig {

    @EzyProperty(PropertyNames.RESOURCE_UPLOAD_ENABLE)
    private boolean resourceUploadEnable = false;

    private EzyBeanContext context;

    @Override
    public void config() {
        if (resourceUploadEnable) {
            ResourceUploadManager resourceUploadManager = createUploadManager(context);
            FileUploader fileUploader = new FileUploader(resourceUploadManager);
            context.getSingletonFactory().addSingleton(resourceUploadManager);
            context.getSingletonFactory().addSingleton(fileUploader);
        }
    }
}
