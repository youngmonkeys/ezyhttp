package com.tvd12.ezyhttp.server.core.resources;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.tvd12.ezyfox.function.EzySupplier;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.util.EzyLoggable;

public class ResourceLoader extends EzyLoggable {
    
    private static final String PROTOCOL_FILE = "file";
    private static final String PROTOCOL_JAR = "jar";
    private static final String PROTOCOL_JAR_PREFIX = "jar:/";
	
	public List<String> listResources(String rootPath) {
		return listResources(rootPath, Collections.emptySet());
	}
	
	public List<String> listResources(String rootPath, Set<String> regexes) {
		List<String> answer = new ArrayList<>();
		Set<URL> resourceURLs = getResourceURLs(rootPath);
        if(resourceURLs.isEmpty()) {
            listResourcesByFolder(new File(rootPath), regexes, rootPath, answer);
        }
        else {
            for(URL url : resourceURLs) {
                listResourcesByURL(url, regexes, rootPath, answer);
            }
        }
		return answer;
	}
	
	protected void listResourcesByURL(
	        URL url,
            Set<String> regexes,
            String rootPath,
            List<String> answer
    ) {
	    if (url.getProtocol().equals(PROTOCOL_FILE)) {
	        listResourcesByFileURL(url, regexes, rootPath, answer);
	    }
	    else if(url.getProtocol().equals(PROTOCOL_JAR)) {
	        listResourcesByJarURL(url, regexes, rootPath, answer);
	    }
	}
	
	protected void listResourcesByFileURL(
	        URL url,
	        Set<String> regexes,
	        String rootPath,
	        List<String> answer
    ) {
	    listResourcesByFolder(
            new File(url.getPath()), 
            regexes,
            rootPath,
            answer
        );
    }
	
	protected void listResourcesByFolder(
            File rootFolder,
            Set<String> regexes,
            String rootPath,
            List<String> answer
    ) {
        Queue<File> folders = new LinkedList<>();
        folders.offer(rootFolder);
        
        while(folders.size() > 0) {
            File folder = folders.poll();
            File[] fileList = listFile(folder);

            for(File resource : fileList) {
                String relativePath = resource
                        .toString()
                        .substring(rootFolder.toString().length() + 1);
                String fullPath = rootPath + "/" + relativePath;
                boolean addable = regexes.isEmpty();
                for(String regex : regexes) {
                    if(fullPath.matches(regex)) {
                        addable = true;
                        break;
                    }
                }
                if(addable && resource.isFile()) {
                    answer.add(fullPath);
                }
                if(resource.isDirectory()) {
                    folders.offer(resource);
                }
            }
        }
    }
	
	protected void listResourcesByJarURL(
            URL url,
            Set<String> regexes,
            String rootPath,
            List<String> answer
    ) {
	    String jarPath = url.getPath().substring(
	        PROTOCOL_JAR_PREFIX.length(), 
            url.getPath().indexOf("!")
        );
        JarFile jar = getJarFile(jarPath);
        Enumeration<JarEntry> entries = jar.entries();
        
        while(entries.hasMoreElements()) {
            String name = entries.nextElement().getName();
            if(name.startsWith(rootPath)) {
                boolean addable = regexes.isEmpty();
                for(String regex : regexes) {
                    if(name.matches(regex)) {
                        addable = true;
                        break;
                    }
                }
                if(addable && isFileElement(name)) {
                    answer.add(name);
                }
            }
        }
	}
	
	protected File[] listFile(File folder) {
	    File[] files = folder.listFiles();
	    return files != null ? files : new File[0];
	}
	
	protected boolean isFileElement(String elementName) {
	    return !elementName.endsWith("/") && !elementName.endsWith("\\");
	}
	
	protected JarFile getJarFile(String filePath) {
	    try {
            return new JarFile(URLDecoder.decode(filePath, EzyStrings.UTF_8));
        } catch (Exception e) {
            return null;
        }
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
