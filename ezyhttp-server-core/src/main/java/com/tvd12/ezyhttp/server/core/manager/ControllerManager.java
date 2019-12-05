package com.tvd12.ezyhttp.server.core.manager;

import java.util.HashMap;
import java.util.Map;

public class ControllerManager {

	protected final Map<String, Object> controllers;
	
	public ControllerManager() {
		this.controllers = new HashMap<>();
	}
	
	public void addController(String uri, Object controller) {
		this.controllers.put(uri, controller);
	}
	
	public Object getController(String uri) {
		Object controller = controllers.get(uri);
		return controller;
	}
	
}
