package com.tvd12.ezyhttp.server.core.reflect;

import static com.tvd12.ezyhttp.server.core.annotation.Annotations.REQUEST_HANDLER_ANNOTATIONS;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.tvd12.ezyfox.reflect.EzyClass;
import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.server.core.annotation.Controller;

import lombok.Getter;

@Getter
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ControllerProxy {

	protected final EzyClass clazz;
	protected final Object instance;
	protected final String requestURI;
	protected final List<RequestHandlerMethod> requestHandlerMethods;
	
	public ControllerProxy(Object instance) {
		this.instance = instance;
		this.clazz = new EzyClass(instance.getClass());
		this.requestURI = getRequestURI();
		this.requestHandlerMethods = fetchRequestHandlerMethods();
	}
	
	protected String getRequestURI() {
		Controller annotation = clazz.getAnnotation(Controller.class);
		return annotation.value();
	}
	
	protected List<RequestHandlerMethod> fetchRequestHandlerMethods() {
		List<RequestHandlerMethod> map = new ArrayList<>();
		List<EzyMethod> methods = clazz.getMethods(m -> isRequestHandlerMethod(m));
		for(EzyMethod method : methods) {
			RequestHandlerMethod m = new RequestHandlerMethod(requestURI, method);
			map.add(m);
		}
		return map;
	}
	
	protected boolean isRequestHandlerMethod(EzyMethod method) {
		for(Class annClass : REQUEST_HANDLER_ANNOTATIONS) {
			Annotation annotation = method.getAnnotation(annClass);
			if(annotation != null)
				return true;
		}
		return false;
	}
	
	public String getControllerName() {
		return clazz.getClazz().getSimpleName();
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(clazz.getName())
				.append("(\n")
					.append("\tinstance: ").append(instance).append(",\n")
					.append("\trequestHandlerMethods: ").append(requestHandlerMethods).append("\n")
				.append(")")
				.toString();
	}
	
}
