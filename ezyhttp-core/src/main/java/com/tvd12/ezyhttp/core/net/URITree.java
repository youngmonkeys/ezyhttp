package com.tvd12.ezyhttp.core.net;

import java.util.HashMap;
import java.util.Map;

public class URITree {

	protected String uri;
	protected Map<String, URITree> children;
	
	public String addURI(String uri) {
		URITree lastChild = this;
		String[] paths = uri.split("/");
		for(int i = 0 ; i < paths.length ; ++i) {
			if(lastChild.children == null)
				lastChild.children = new HashMap<>();
			String path = paths[i];
			if(PathVariables.isPathVariable(path))
				path = "{}";
			URITree child = lastChild.children.get(path);
			if(child == null) {
				child = new URITree();
				lastChild.children.put(path, child);
			}
			lastChild = child;
		}
		String oldURI = lastChild.uri;
		lastChild.uri = uri;
		return oldURI;
	}
	
	public String getMatchedURI(String uri) {
		URITree lastChild = this;
		String[] paths = uri.split("/");
		for(int i = 0 ; i < paths.length ; ++i) {
			if(lastChild.children == null)
				return null;
			URITree child = lastChild.children.get(paths[i]);
			if(child == null)
				child = lastChild.children.get("{}");
			if(child == null)
				return null;
			lastChild = child;
		}
		return lastChild.uri;
		
	}
	
	public boolean containsURI(String uri) {
		String matched = getMatchedURI(uri);
		return matched != null;
	}
	
	@Override
	public String toString() {
		if(uri != null)
			return uri;
		if(children == null)
			return "";
		return children.toString();
		
	}
	
}
