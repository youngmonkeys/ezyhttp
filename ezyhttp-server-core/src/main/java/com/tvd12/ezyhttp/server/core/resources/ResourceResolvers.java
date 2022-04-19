package com.tvd12.ezyhttp.server.core.resources;

import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.*;
import static com.tvd12.ezyhttp.server.core.resources.ResourceResolver.DEFAULT_RESOURCE_LOCATION;

import com.tvd12.ezyfox.bean.EzyPropertyFetcher;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.core.resources.ResourceUploadManager;

public final class ResourceResolvers {

    private ResourceResolvers() {}

    public static ResourceResolver createResourdeResolver(EzyPropertyFetcher propertyFetcher) {
        boolean resourceEnable = propertyFetcher
                .getProperty(RESOURCE_ENABLE, boolean.class, false);
        if (!resourceEnable)
            return null;
        String[] resourceLocations = propertyFetcher
                .getProperty(RESOURCE_LOCATIONS, String[].class);
        if (resourceLocations == null) {
            String resourceLocation = propertyFetcher
                    .getProperty(RESOURCE_LOCATION, String.class, DEFAULT_RESOURCE_LOCATION);
            resourceLocations = new String[] { resourceLocation };
        }
        String pattern = propertyFetcher.getProperty(RESOURCE_PATTERN, String.class);
        String[] patterns = pattern != null ? new String[] {pattern} : new String[0];
        ResourceResolver resourceResolver = new ResourceResolver();
        resourceResolver.register(resourceLocations, patterns);
        return resourceResolver;
    }

    public static ResourceDownloadManager createDownloadManager(EzyPropertyFetcher propertyFetcher) {
        int capacity = propertyFetcher
                .getProperty(RESOURCE_DOWNLOAD_CAPACITY, int.class, ResourceDownloadManager.DEFAULT_CAPACITY);
        int threadPoolSize = propertyFetcher
                .getProperty(RESOURCE_DOWNLOAD_THREAD_POOL_SIZE, int.class, ResourceDownloadManager.DEFAULT_THREAD_POOL_SIZE);
        int bufferSize = propertyFetcher
                .getProperty(RESOURCE_DOWNLOAD_BUFFER_SIZE, int.class, ResourceDownloadManager.DEFAULT_BUFFER_SIZE);
        return new ResourceDownloadManager(capacity, threadPoolSize, bufferSize);
    }

    public static ResourceUploadManager createUploadManager(EzyPropertyFetcher propertyFetcher) {
        int capacity = propertyFetcher
                .getProperty(RESOURCE_UPLOAD_CAPACITY, int.class, ResourceUploadManager.DEFAULT_CAPACITY);
        int threadPoolSize = propertyFetcher
                .getProperty(RESOURCE_UPLOAD_THREAD_POOL_SIZE, int.class, ResourceUploadManager.DEFAULT_THREAD_POOL_SIZE);
        int bufferSize = propertyFetcher
                .getProperty(RESOURCE_UPLOAD_BUFFER_SIZE, int.class, ResourceUploadManager.DEFAULT_BUFFER_SIZE);
        return new ResourceUploadManager(capacity, threadPoolSize, bufferSize);
    }
}
