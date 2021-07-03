package com.tvd12.ezyhttp.server.core.resources;

import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.RESOURCE_DOWNLOAD_BUFFER_SIZE;
import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.RESOURCE_DOWNLOAD_CAPACITY;
import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.RESOURCE_DOWNLOAD_THREAD_POOL_SIZE;
import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.RESOURCE_ENABLE;
import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.RESOURCE_LOCATION;
import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.RESOURCE_LOCATIONS;
import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.RESOURCE_PATTERN;
import static com.tvd12.ezyhttp.server.core.resources.ResourceDownloadManager.DEFAULT_BUFFER_SIZE;
import static com.tvd12.ezyhttp.server.core.resources.ResourceDownloadManager.DEFAULT_CAPACITY;
import static com.tvd12.ezyhttp.server.core.resources.ResourceDownloadManager.DEFAULT_THREAD_POOL_SIZE;
import static com.tvd12.ezyhttp.server.core.resources.ResourceResolver.DEFAULT_RESOURCE_LOCATION;

import com.tvd12.ezyfox.bean.EzyPropertyFetcher;

public final class ResourceResolvers {

	public ResourceResolvers() {}
	
	public static ResourceResolver createResourdeResolver(EzyPropertyFetcher propertyFetcher) {
		Boolean resourceEnable = propertyFetcher
				.getProperty(RESOURCE_ENABLE, Boolean.class);
		if(resourceEnable == null || !resourceEnable)
			return null;
		String[] resourceLocations = propertyFetcher
				.getProperty(RESOURCE_LOCATIONS, String[].class);
		if(resourceLocations == null) {
			String resourceLocation = propertyFetcher
					.getProperty(RESOURCE_LOCATION, String.class);
			if(resourceLocation == null)
				resourceLocation = DEFAULT_RESOURCE_LOCATION;
			resourceLocations = new String[] { resourceLocation };
		}
		String pattern = propertyFetcher.getProperty(RESOURCE_PATTERN, String.class);
		String[] patterns = pattern != null ? new String[] {pattern} : new String[0];
		ResourceResolver resourceResolver = new ResourceResolver();
		resourceResolver.register(resourceLocations, patterns);
		return resourceResolver;
	}
	
	public static ResourceDownloadManager createDownloadManager(EzyPropertyFetcher propertyFetcher) {
		Integer capacity = propertyFetcher
				.getProperty(RESOURCE_DOWNLOAD_CAPACITY, Integer.class);
		if(capacity == null)
			capacity = DEFAULT_CAPACITY;
		Integer threadPoolSize = propertyFetcher
				.getProperty(RESOURCE_DOWNLOAD_THREAD_POOL_SIZE, Integer.class);
		if(threadPoolSize == null)
			threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
		Integer bufferSize = propertyFetcher
				.getProperty(RESOURCE_DOWNLOAD_BUFFER_SIZE, Integer.class);
		if(bufferSize == null)
			bufferSize = DEFAULT_BUFFER_SIZE;
		return new ResourceDownloadManager(capacity, threadPoolSize, bufferSize);
	}
	
}
