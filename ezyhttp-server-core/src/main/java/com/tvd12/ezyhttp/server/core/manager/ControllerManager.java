package com.tvd12.ezyhttp.server.core.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;

public class ControllerManager {

	@Getter
	protected final List<Object> controllers;
	
	public ControllerManager() {
		this.controllers = new ArrayList<>();
	}
	
	public void addController(Object controller) {
		this.controllers.add(controller);
	}
	
	public void addControllers(Collection<?> controllers) {
		for(Object controller : controllers)
			addController(controller);
	}
	
}
