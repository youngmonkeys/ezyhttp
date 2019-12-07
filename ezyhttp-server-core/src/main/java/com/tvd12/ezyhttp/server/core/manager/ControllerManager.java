package com.tvd12.ezyhttp.server.core.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.util.ControllerApplications;

public class ControllerManager {

	protected final Map<String, Object> controllers;
	
	public ControllerManager() {
		this.controllers = new HashMap<>();
	}
	
	public void addController(Object controller) {
		Class<?> controllerClass = controller.getClass();
		Controller annotation = controllerClass.getAnnotation(Controller.class);
		String uri = ControllerApplications.getURI(annotation);
		addController(uri, controller);
	}
	
	public void addController(String uri, Object controller) {
		this.controllers.put(uri, controller);
	}
	
	public void addControllers(Collection<?> controllers) {
		for(Object controller : controllers)
			addController(controller);
	}
	
	public Object getController(String uri) {
		Object controller = controllers.get(uri);
		return controller;
	}
	
}
