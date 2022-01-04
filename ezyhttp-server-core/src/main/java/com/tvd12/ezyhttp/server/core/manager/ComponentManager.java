package com.tvd12.ezyhttp.server.core.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.core.json.ObjectMapperBuilder;
import com.tvd12.ezyhttp.server.core.handler.RequestResponseWatcher;
import com.tvd12.ezyhttp.server.core.handler.UnhandledErrorHandler;
import com.tvd12.ezyhttp.server.core.request.RequestURI;
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
	
	private Set<String> managementURIs;
	@Setter
	private ViewContext viewContext;
	private ObjectMapper objectMapper;
	private DataConverters dataConverters;
	private ControllerManager controllerManager;
	private InterceptorManager interceptorManager;
	private RequestHandlerManager requestHandlerManager;
	private ExceptionHandlerManager exceptionHandlerManager;
	private UnhandledErrorHandler unhandledErrorHandler;
	private List<RequestResponseWatcher> requestResponseWatchers;
	
	private static final ComponentManager INSTANCE = new ComponentManager();
	
	private ComponentManager() {
	    this.managementURIs = new HashSet<>();
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
	
	public void addManagementURIs(Collection<String> uris) {
	    this.managementURIs.addAll(uris);
	}
	
	public void appendManagementURIs(Iterable<RequestURI> uris) {
	    for (RequestURI uri : uris) {
	        if (uri.isManagement()) {
	            this.managementURIs.add(uri.getUri());
	        }
	    }
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
		this.managementURIs.clear();
		this.dataConverters.destroy();
		this.controllerManager.destroy();
		this.interceptorManager.destroy();
		this.requestHandlerManager.destroy();
		this.exceptionHandlerManager.destroy();
		this.requestResponseWatchers.clear();
	}
}
