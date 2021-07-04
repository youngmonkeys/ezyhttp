package com.tvd12.ezyhttp.server.core.manager;

import java.util.Set;

import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.server.core.view.ViewContext;

import lombok.Getter;
import lombok.Setter;

@Getter
public final class ComponentManager {
	
	@Setter
	private int serverPort;
	@Setter
	private int managmentPort;
	@Setter
	private Set<String> managementURIs;
	@Setter
	private ViewContext viewContext;
	private DataConverters dataConverters;
	private ControllerManager controllerManager;
	private InterceptorManager interceptorManager;
	private RequestHandlerManager requestHandlerManager;
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
