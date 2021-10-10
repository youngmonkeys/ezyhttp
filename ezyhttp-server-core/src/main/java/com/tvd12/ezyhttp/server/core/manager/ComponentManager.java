package com.tvd12.ezyhttp.server.core.manager;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.core.json.ObjectMapperBuilder;
import com.tvd12.ezyhttp.server.core.handler.UncaughtErrorHandler;
import com.tvd12.ezyhttp.server.core.view.ViewContext;

import lombok.Getter;
import lombok.Setter;

@Getter
public final class ComponentManager implements EzyDestroyable {
	
	@Setter
	private int serverPort;
	@Setter
	private int managmentPort;
	@Setter
	private Set<String> managementURIs;
	@Setter
	private ViewContext viewContext;
	private ObjectMapper objectMapper;
	private DataConverters dataConverters;
	private ControllerManager controllerManager;
	private InterceptorManager interceptorManager;
	private RequestHandlerManager requestHandlerManager;
	private ExceptionHandlerManager exceptionHandlerManager;
	private UncaughtErrorHandler uncaughtErrorHandler;
	
	private static final ComponentManager INSTANCE = new ComponentManager();
	
	private ComponentManager() {
		this.objectMapper = new ObjectMapperBuilder().build();
		this.dataConverters = new DataConverters(objectMapper);
		this.controllerManager = new ControllerManager();
		this.interceptorManager = new InterceptorManager();
		this.requestHandlerManager = new RequestHandlerManager();
		this.exceptionHandlerManager = new ExceptionHandlerManager();
	}
	
	public static ComponentManager getInstance() {
		return INSTANCE;
	}
	
	public void setUncaughtErrorHandler(List<UncaughtErrorHandler> handlers) {
	    if (handlers.size() > 0) {
	        this.uncaughtErrorHandler = handlers.get(0);
	    }
	}
	
	@Override
	public void destroy() {
		this.viewContext = null;
		this.uncaughtErrorHandler = null;
		this.dataConverters.destroy();
		this.controllerManager.destroy();
		this.interceptorManager.destroy();
		this.requestHandlerManager.destroy();
		this.exceptionHandlerManager.destroy();
	}
}
