package com.tvd12.ezyhttp.server.core;

import static com.tvd12.ezyhttp.core.constant.Constants.DEFAULT_PROPERTIES_FILE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.reflect.EzyReflection;
import com.tvd12.ezyfox.reflect.EzyReflectionProxy;
import com.tvd12.ezyhttp.core.annotation.BodyConvert;
import com.tvd12.ezyhttp.core.annotation.Interceptor;
import com.tvd12.ezyhttp.core.annotation.StringConvert;
import com.tvd12.ezyhttp.core.codec.DataConverters;
import com.tvd12.ezyhttp.server.core.annotation.ApplicationBootstrap;
import com.tvd12.ezyhttp.server.core.annotation.ComponentClasses;
import com.tvd12.ezyhttp.server.core.annotation.ComponentsScan;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.ExceptionHandler;
import com.tvd12.ezyhttp.server.core.annotation.PropertiesSources;
import com.tvd12.ezyhttp.server.core.annotation.Service;
import com.tvd12.ezyhttp.server.core.asm.ExceptionHandlersImplementer;
import com.tvd12.ezyhttp.server.core.asm.RequestHandlersImplementer;
import com.tvd12.ezyhttp.server.core.constant.PropertyNames;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;
import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.manager.ControllerManager;
import com.tvd12.ezyhttp.server.core.manager.ExceptionHandlerManager;
import com.tvd12.ezyhttp.server.core.manager.InterceptorManager;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.core.request.RequestURI;
import com.tvd12.ezyhttp.server.core.util.ServiceAnnotations;
import com.tvd12.properties.file.reader.BaseFileReader;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ApplicationContextBuilder implements EzyBuilder<ApplicationContext> {

	protected final Properties properties;
	protected final Set<String> packageToScans;
	protected final Set<Class> componentClasses;
	protected final Set<String> propertiesSources;
	protected final DataConverters dataConverters;
	protected final ComponentManager componentManager;
	protected final ControllerManager controllerManager;
	protected final InterceptorManager interceptorManager;
	protected final RequestHandlerManager requestHandlerManager;
	protected final ExceptionHandlerManager exceptionHandlerManager;
	
	public ApplicationContextBuilder() {
		this.properties = defaultProperties();
		this.packageToScans = new HashSet<>();
		this.componentClasses = new HashSet<>();
		this.propertiesSources = new HashSet<>();
		this.componentManager = ComponentManager.getInstance();
		this.dataConverters = componentManager.getDataConverters();
		this.controllerManager = componentManager.getControllerManager();
		this.interceptorManager = componentManager.getInterceptorManager();
		this.requestHandlerManager = componentManager.getRequestHandlerManager();
		this.exceptionHandlerManager = componentManager.getExceptionHandlerManager();
	}
	
	public Properties defaultProperties() {
		Properties props = new Properties();
		props.put(PropertyNames.SERVER_PORT, 8080);
		return props;
	}
	
	public ApplicationContextBuilder scan(String packageName) {
		this.packageToScans.add(packageName);
		return this;
	}
	
	public ApplicationContextBuilder scan(String... packageNames) {
		for(String packageName : packageNames)
			scan(packageName);
		return this;
	}
	
	public ApplicationContextBuilder scan(Iterable<String> packageNames) {
		for(String packageName : packageNames)
			scan(packageName);
		return this;
	}
	
	public ApplicationContextBuilder addComponentClass(Class<?> componentClass) {
		ComponentsScan componentsScan = componentClass.getAnnotation(ComponentsScan.class);
		if(componentsScan != null)
			scan(componentsScan.value());
		ComponentClasses componentClasses = componentClass.getAnnotation(ComponentClasses.class);
		if(componentClasses != null)
			addComponentClasses(componentClasses.value());
		PropertiesSources propertiesSources = componentClass.getAnnotation(PropertiesSources.class);
		if(propertiesSources != null)
			addPropertiesSources(propertiesSources.value());
		this.componentClasses.add(componentClass);
		return this;
	}
	
	public ApplicationContextBuilder addComponentClasses(Class<?>... componentClasses) {
		for(Class<?> clazz : componentClasses)
			addComponentClass(clazz);
		return this;
	}
	
	public ApplicationContextBuilder addComponentClasses(Iterable<Class<?>> componentClasses) {
		for(Class<?> clazz : componentClasses)
			addComponentClass(clazz);
		return this;
	}
	
	public ApplicationContextBuilder addPropertiesSource(String source) {
		this.propertiesSources.add(source);
		return this;
	}
	
	public ApplicationContextBuilder addPropertiesSources(String... sources) {
		for(String source : sources)
			addPropertiesSource(source);
		return this;
	}
	
	public ApplicationContextBuilder addPropertiesSources(Iterable<String> sources) {
		for(String source : sources)
			addPropertiesSource(source);
		return this;
	}
	
	public ApplicationContextBuilder addProperty(String name, String value) {
		this.properties.put(name, value);
		return this;
	}
	
	public ApplicationContextBuilder addProperties(Properties properties) {
		this.properties.putAll(properties);
		return this;
	}
	
	public ApplicationContextBuilder addProperties(Map<String, String> properties) {
		this.properties.putAll(properties);
		return this;
	}
	
	@Override
	public ApplicationContext build() {
		EzyReflection reflection = new EzyReflectionProxy(packageToScans);
		EzyBeanContext beanContext = createBeanContext(reflection);
		SimpleApplicationContext context = new SimpleApplicationContext();
		context.setBeanContext(beanContext);
		return context;
	}
	
	protected EzyBeanContext createBeanContext(EzyReflection reflection) {
		Set controllerClasses = reflection.getAnnotatedClasses(Controller.class);
		Set interceptorClases = reflection.getAnnotatedClasses(Interceptor.class);
		Set exceptionHandlerClasses = reflection.getAnnotatedClasses(ExceptionHandler.class);
		Set bodyConverterClasses = reflection.getAnnotatedClasses(BodyConvert.class);
		Set stringConverterClasses = reflection.getAnnotatedClasses(StringConvert.class);
		Set bootstrapClasses = reflection.getAnnotatedClasses(ApplicationBootstrap.class);
		Map<String, Class> serviceClasses = getServiceClasses(reflection); 
		properties.putAll(readPropertiesSources());
		EzyBeanContext beanContext = EzyBeanContext.builder()
				.addProperties(properties)
				.addAllClasses(reflection)
				.addSingletonClasses(componentClasses)
				.addSingletonClasses(serviceClasses)
				.addSingletonClasses(controllerClasses)
				.addSingletonClasses(interceptorClases)
				.addSingletonClasses(exceptionHandlerClasses)
				.addSingletonClasses(bodyConverterClasses)
				.addSingletonClasses(stringConverterClasses)
				.addSingletonClasses(bootstrapClasses)
				.build();
		registerComponents(beanContext);
		addRequestHandlers();
		addExceptionHandlers();
		return beanContext;
	}
	
	protected Map<String, Class> getServiceClasses(EzyReflection reflection) {
		Set<Class<?>> classes = reflection.getAnnotatedClasses(Service.class);
		Map<String, Class> answer = new HashMap<>();
		for(Class<?> clazz : classes) {
			String serviceName = ServiceAnnotations.getServiceName(clazz);
			answer.put(serviceName, clazz);
		}
		return answer;
	}
	
	protected Properties readPropertiesSources() {
		Properties props = new Properties();
		if(propertiesSources.size() > 0) {
			for(String source : propertiesSources)
				props.putAll(readPropertiesSource(source));
		}
		else {
			try {
				props.putAll(readPropertiesSource(DEFAULT_PROPERTIES_FILE));
			}
			catch (Exception e) {}
		}
		return props;
	}
	
	protected Properties readPropertiesSource(String source) {
		BaseFileReader reader = new BaseFileReader();
		Properties props = reader.read(source);
		return props;
	}
	
	protected void registerComponents(EzyBeanContext beanContext) {
		List controllers = beanContext.getSingletons(Controller.class);
		controllerManager.addControllers(controllers);
		List exceptionHandlers = beanContext.getSingletons(ExceptionHandler.class);
		exceptionHandlerManager.addExceptionHandlers(exceptionHandlers);
		List<RequestInterceptor> requestInterceptors = beanContext.getSingletons(Interceptor.class);
		interceptorManager.addRequestInterceptors(requestInterceptors);
		List bodyConverters = beanContext.getSingletons(BodyConvert.class);
		dataConverters.addBodyConverters(bodyConverters);
		List stringConverters = beanContext.getSingletons(StringConvert.class);
		dataConverters.setStringConverters(stringConverters);
	}
	
	protected void addRequestHandlers() {
		List<Object> controllerList = controllerManager.getControllers();
		RequestHandlersImplementer implementer = newRequestHandlersImplementer();
		Map<RequestURI, RequestHandler> requestHandlers = implementer.implement(controllerList);
		requestHandlerManager.addHandlers(requestHandlers);
	}
	
	protected void addExceptionHandlers() {
		List<Object> exceptionHandlerList = exceptionHandlerManager.getExceptionHandlerList();
		ExceptionHandlersImplementer implementer = newExceptionHandlersImplementer();
		Map<Class<?>, UncaughtExceptionHandler> exceptionHandlers = implementer.implement(exceptionHandlerList);
		exceptionHandlerManager.addUncaughtExceptionHandlers(exceptionHandlers);
	}
	
	protected RequestHandlersImplementer newRequestHandlersImplementer() {
		return new RequestHandlersImplementer();
	}
	
	protected ExceptionHandlersImplementer newExceptionHandlersImplementer() {
		return new ExceptionHandlersImplementer();
	}
	
}
