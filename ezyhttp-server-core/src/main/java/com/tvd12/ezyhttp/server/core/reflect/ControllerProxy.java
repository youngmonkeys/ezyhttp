package com.tvd12.ezyhttp.server.core.reflect;

import static com.tvd12.ezyhttp.server.core.annotation.Annotations.REQUEST_HANDLER_ANNOTATIONS;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.tvd12.ezyfox.reflect.EzyClass;
import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;

import lombok.Getter;

@Getter
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ControllerProxy {

	protected final EzyClass clazz;
	protected final Object instance;
	protected final String requestURI;
	protected final List<RequestHandlerMethod> requestHandlerMethods;
	protected final List<ExceptionHandlerMethod> exceptionHandlerMethods;
	
	public ControllerProxy(Object instance) {
		this.instance = instance;
		this.clazz = new EzyClass(instance.getClass());
		this.requestURI = getRequestURI();
		this.requestHandlerMethods = fetchRequestHandlerMethods();
		this.exceptionHandlerMethods = fetchExceptionHandlerMethods();
	}
	
	protected String getRequestURI() {
		Controller annotation = clazz.getAnnotation(Controller.class);
		return annotation.value();
	}
	
	protected List<RequestHandlerMethod> fetchRequestHandlerMethods() {
		List<RequestHandlerMethod> list = new ArrayList<>();
		List<EzyMethod> methods = clazz.getMethods(m -> isRequestHandlerMethod(m));
		for(EzyMethod method : methods) {
			RequestHandlerMethod m = new RequestHandlerMethod(requestURI, method);
			list.add(m);
		}
		return list;
	}
	
	public List<ExceptionHandlerMethod> fetchExceptionHandlerMethods() {
		List<ExceptionHandlerMethod> list = new ArrayList<>();
		List<EzyMethod> methods = clazz.getMethods(m -> m.isAnnotated(TryCatch.class));
		for(EzyMethod method : methods) {
			ExceptionHandlerMethod m = new ExceptionHandlerMethod(method);
			list.add(m);
		}
		return list;
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
