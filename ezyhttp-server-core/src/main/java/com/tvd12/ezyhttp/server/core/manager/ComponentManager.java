package com.tvd12.ezyhttp.server.core.manager;

import com.tvd12.ezyhttp.core.codec.DataConverters;

import lombok.Getter;

public final class ComponentManager {

	@Getter
	private DataConverters dataConverters;
	@Getter
	private ControllerManager controllerManager;
	@Getter
	private InterceptorManager interceptorManager;
	@Getter
	private RequestHandlerManager requestHandlerManager;
	@Getter
	private ExceptionHandlerManager exceptionHandlerManager;
	
	private static final ComponentManager INSTANCE = new ComponentManager();
	
	private ComponentManager() {
		this.dataConverters = new DataConverters();
		this.controllerManager = new ControllerManager();
		this.interceptorManager = new InterceptorManager();
		this.requestHandlerManager = new RequestHandlerManager();
		this.exceptionHandlerManager = new ExceptionHandlerManager();
	}
	
	public static ComponentManager getInstance() {
		return INSTANCE;
	}
	
}
