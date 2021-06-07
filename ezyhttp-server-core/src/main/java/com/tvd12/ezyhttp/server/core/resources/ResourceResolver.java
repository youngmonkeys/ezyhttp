package com.tvd12.ezyhttp.server.core.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tvd12.ezyfox.util.EzyFileUtil;
import com.tvd12.reflections.util.Sets;

import lombok.Getter;

public class ResourceResolver {

	@Getter
	protected final Map<String, Resource> resources = new HashMap<>();
	protected final ResourceLoader resourceLoader = new ResourceLoader();

	public final static String DEFAULT_RESOURCE_LOCATION = "templates";
	public final static String DEFAULT_FILE_PATH_PATTERN = "[.\\/\\w\\d_-]+[.][\\w\\d_-]+";
	
	public void register(String localtion) {
		register(localtion, DEFAULT_FILE_PATH_PATTERN);
	}

	public void register(String[] locations) {
		register(locations, DEFAULT_FILE_PATH_PATTERN);
	}
	
	public void register(String[] locations, String... filePathRegexes) {
		for(String location : locations)
			register(location, filePathRegexes);
	}
	
	public void register(String location, String... filePathRegexes) {
		String trimLocation = location.trim();
		List<String> resourceFiles = resourceLoader
				.listResources(trimLocation, Sets.newHashSet(filePathRegexes));
		for(String res : resourceFiles) {
			String resourceURI = res.substring(trimLocation.length() + 1);
			String extension = EzyFileUtil.getFileExtension(resourceURI);
			resources.put(resourceURI, new Resource(res, resourceURI, extension));
		}
	}
	
}
