package com.tvd12.ezyhttp.server.core.resources;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.tvd12.ezyfox.function.EzySupplier;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.util.EzyLoggable;

public class ResourceLoader extends EzyLoggable {
	
	public List<String> listResources(String rootPath) {
		return listResources(rootPath, Collections.emptySet());
	}
	
	public List<String> listResources(String rootPath, Set<String> regexes) {
		List<String> answer = new ArrayList<>();
		Queue<String> folders = new LinkedList<>();
		folders.add(rootPath);
		while(true) {
			if(folders.isEmpty())
				break;
			File folder;
			String resourcePath = folders.poll();
			Set<URL> resourceURLs = getResourceURLs(resourcePath);
			for(URL url : resourceURLs) {
    			if(EzyStrings.isNoContent(url.getPath())) {
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
    				if(regexes.isEmpty()) {
    					answer.add(fullPath);
    				}
    				else {
    					for(String regex : regexes) {
    						if(fullPath.matches(regex)) {
    							answer.add(fullPath);
    							break;
    						}
    					}
    				}
    			}
			}
		}
		return answer;
	}
	
	protected Set<URL> getResourceURLs(String resource) {
	    Set<URL> answer = new HashSet<>();
		String[] resources = {resource, "/" + resource};
		for(String res : resources) {
	        addURLsToSet(answer, () -> getContextClassLoader().getResources(res));
	        addURLsToSet(answer, () -> getClass().getClassLoader().getResources(res));
	        addURLsToSet(answer, () -> ClassLoader.getSystemResources(res));
	        addURLToSet(answer, getContextClassLoader().getResource(res));
	        addURLToSet(answer, getClass().getResource(res));
	        addURLToSet(answer, getClass().getClassLoader().getResource(res));
	        addURLToSet(answer, ClassLoader.getSystemResource(res));
		}
		return answer;
	}
	
	private void addURLsToSet(
	        Set<URL> answer, 
	        EzySupplier<Enumeration<URL>> supplier) {
	    try {
	        Enumeration<URL> urls = supplier.get();
	        addURLsToSet(answer, urls);
	    }
	    catch (Exception e) {
	        // do nothing
        }
	}
	
	private void addURLsToSet(Set<URL> answer, Enumeration<URL> urls) {
	    if(urls != null) {
	        while(urls.hasMoreElements()) {
	            answer.add(urls.nextElement());
	        }
	    }
	}
	
	private void addURLToSet(Set<URL> answer, URL url) {
	    if(url != null)
	        answer.add(url);
	}

	private ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
}
