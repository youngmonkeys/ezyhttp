package com.tvd12.ezyhttp.server.core;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.reflect.EzyReflection;
import com.tvd12.ezyfox.reflect.EzyReflectionProxy;
import com.tvd12.ezyhttp.server.core.annotation.ApplicationBootstrap;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.asm.RequestHandlersImplementer;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.manager.ControllerManager;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.core.request.RequestURI;

public class ApplicationContextBuilder implements EzyBuilder<ApplicationContext> {

	protected final Set<String> packageToScans;
	protected final ComponentManager componentManager;
	protected final ControllerManager controllerManager;
	protected final RequestHandlerManager requestHandlerManager;
	
	public ApplicationContextBuilder() {
		this.packageToScans = new HashSet<>();
		this.componentManager = ComponentManager.getInstance();
		this.controllerManager = componentManager.getControllerManager();
		this.requestHandlerManager = componentManager.getRequestHandlerManager();
	}
	
	public ApplicationContextBuilder scan(String packageName) {
		this.packageToScans.add(packageName);
		return this;
	}
	
	@Override
	public ApplicationContext build() {
		EzyReflection reflection = new EzyReflectionProxy(packageToScans);
		EzyBeanContext beanContext = createBeanContext(reflection);
		ApplicationContext context = new ApplicationContext();
		context.setBeanContext(beanContext);
		return context;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected EzyBeanContext createBeanContext(EzyReflection reflection) {
		Set controllerClasses = reflection.getAnnotatedClasses(Controller.class);
		Set bootstrapClasses = reflection.getAnnotatedClasses(ApplicationBootstrap.class);
		EzyBeanContext beanContext = EzyBeanContext.builder()
				.addAllClasses(reflection)
				.addSingletonClasses(controllerClasses)
				.addSingletonClasses(bootstrapClasses)
				.build();
		registerComponents(beanContext);
		addRequestHandlers();
		return beanContext;
	}
	
	@SuppressWarnings("rawtypes")
	protected void registerComponents(EzyBeanContext beanContext) {
		List controllers = beanContext.getSingletons(Controller.class);
		controllerManager.addControllers(controllers);
	}
	
	protected void addRequestHandlers() {
		List<Object> controllerList = controllerManager.getControllerList();
		RequestHandlersImplementer implementer = newRequestHandlersImplementer();
		Map<RequestURI, RequestHandler> requestHandlers = implementer.implement(controllerList);
		requestHandlerManager.addHandlers(requestHandlers);
	}
	
	protected RequestHandlersImplementer newRequestHandlersImplementer() {
		return new RequestHandlersImplementer();
	}
}
