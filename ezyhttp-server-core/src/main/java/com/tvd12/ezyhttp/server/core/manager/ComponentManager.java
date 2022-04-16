package com.tvd12.ezyhttp.server.core.manager;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.core.json.ObjectMapperBuilder;
import com.tvd12.ezyhttp.server.core.handler.RequestResponseWatcher;
import com.tvd12.ezyhttp.server.core.handler.UnhandledErrorHandler;
import com.tvd12.ezyhttp.server.core.view.ViewContext;

import lombok.Getter;
import lombok.Setter;

@Getter
public final class ComponentManager implements EzyDestroyable {
	
    @Setter
    private boolean debug;
	@Setter
	private int serverPort;
	@Setter
	private int managmentPort;
	@Setter 
	private boolean exposeMangementURIs;
	
	@Setter
	private ViewContext viewContext;
	private final ObjectMapper objectMapper;
	private final DataConverters dataConverters;
	private final ControllerManager controllerManager;
	private final InterceptorManager interceptorManager;
	private final RequestHandlerManager requestHandlerManager;
	private final ExceptionHandlerManager exceptionHandlerManager;
	private UnhandledErrorHandler unhandledErrorHandler;
	private final List<RequestResponseWatcher> requestResponseWatchers;
	
	private static final ComponentManager INSTANCE = new ComponentManager();
	
	private ComponentManager() {
		this.objectMapper = new ObjectMapperBuilder().build();
		this.dataConverters = new DataConverters(objectMapper);
		this.requestResponseWatchers = new ArrayList<>();
		this.controllerManager = new ControllerManager();
		this.interceptorManager = new InterceptorManager();
		this.requestHandlerManager = new RequestHandlerManager();
		this.exceptionHandlerManager = new ExceptionHandlerManager();
	}
	
	public static ComponentManager getInstance() {
		return INSTANCE;
	}
	
	public void setUnhandledErrorHandler(List<UnhandledErrorHandler> handlers) {
	    if (handlers.size() > 0) {
	        this.unhandledErrorHandler = handlers.get(0);
	    }
	}
	
	public void addRequestResponseWatchers(List<RequestResponseWatcher> watchers) {
	    this.requestResponseWatchers.addAll(watchers);
    }
	
	@Override
	public void destroy() {
		this.viewContext = null;
		this.exposeMangementURIs = false;
		this.unhandledErrorHandler = null;
		this.dataConverters.destroy();
		this.controllerManager.destroy();
		this.interceptorManager.destroy();
		this.requestHandlerManager.destroy();
		this.exceptionHandlerManager.destroy();
		this.requestResponseWatchers.clear();
	}
}
