package com.tvd12.ezyhttp.server.core.resources;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.tvd12.ezyfox.util.EzyLoggable;

public class ResourceLoader extends EzyLoggable {
	
	public List<String> listResources(String rootPath, Set<String> regexes) {
		List<String> answer = new ArrayList<>();
		Queue<String> folders = new LinkedList<>();
		folders.add(rootPath);
		while(true) {
			if(folders.isEmpty())
				break;
			File folder;
			String resourcePath = folders.poll();
			URL url = getResourceURL(resourcePath);
			if(url == null || url.getPath() == null) {
				folder = new File(resourcePath);
			}
			else {
				folder = new File(url.getPath());
			}
			String[] fileList = folder.list();
			if(fileList == null)
				continue;
			for(String resource : fileList) {
				String fullPath = resourcePath + "/" + resource;
				folders.offer(fullPath);
				for(String regex : regexes) {
					if(fullPath.matches(regex)) {
						answer.add(fullPath);
						break;
					}
				}
			}
		}
		return answer;
	}
	
	private URL getResourceURL(String resource) {
		String[] resources = {resource, "/" + resource};
		URL url = null;
		for(String res : resources) {
			url = getContextClassLoader().getResource(res);
			if(url == null)
				url = getClass().getResource(res);
			if(url != null)
				break;
		}
		return url;
			
	}

	private ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
}
