package com.tvd12.ezyhttp.server.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.reflect.EzyReflection;
import com.tvd12.ezyfox.reflect.EzyReflectionProxy;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.manager.ControllerManager;

public class ApplicationContextBuilder implements EzyBuilder<ApplicationContext> {

	protected Set<String> packageToScans;
	
	public ApplicationContextBuilder() {
		this.packageToScans = new HashSet<>();
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
		EzyBeanContext beanContext = EzyBeanContext.builder()
				.addAllClasses(reflection)
				.addSingletonClasses(controllerClasses)
				.build();
		registerComponents(beanContext);
		return beanContext;
	}
	
	@SuppressWarnings("rawtypes")
	protected void registerComponents(EzyBeanContext beanContext) {
		ComponentManager componentManager = ComponentManager.getInstance();
		ControllerManager controllerManager = componentManager.getControllerManager();
		List controllers = beanContext.getSingletons(Controller.class);
		controllerManager.addControllers(controllers);
	}
}
