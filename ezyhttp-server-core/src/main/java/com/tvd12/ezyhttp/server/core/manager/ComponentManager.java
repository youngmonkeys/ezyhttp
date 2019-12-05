package com.tvd12.ezyhttp.server.core.manager;

import lombok.Getter;

public final class ComponentManager {

	@Getter
	private ControllerManager controllerManager;
	@Getter
	private RequestHandlerManager requestHandlerManager;
	
	private static final ComponentManager INSTANCE = new ComponentManager();
	
	private ComponentManager() {
		this.controllerManager = new ControllerManager();
		this.requestHandlerManager = new RequestHandlerManager();
	}
	
	public static ComponentManager getInstance() {
		return INSTANCE;
	}
	
}
