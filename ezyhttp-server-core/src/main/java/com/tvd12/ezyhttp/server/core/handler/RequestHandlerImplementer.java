package com.tvd12.ezyhttp.server.core.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.atomic.AtomicInteger;

import com.tvd12.ezyfox.asm.EzyFunction;
import com.tvd12.ezyfox.asm.EzyFunction.EzyBody;
import com.tvd12.ezyfox.asm.EzyInstruction;
import com.tvd12.ezyfox.reflect.EzyClass;
import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyfox.reflect.EzyMethods;
import com.tvd12.ezyfox.reflect.EzyReflections;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import lombok.Setter;

public class RequestHandlerImplementer extends EzyLoggable {

	@Setter
	private static boolean debug;
	protected final ControllerProxy controller;
	protected final RequestHandlerMethod handlerMethod;
	
	protected final static String PARAMETER_PREFIX = "param";
	protected final static AtomicInteger COUNT = new AtomicInteger(0);
	
	public RequestHandlerImplementer(
			ControllerProxy controller, RequestHandlerMethod handlerMethod) {
		this.controller = controller;
		this.handlerMethod = handlerMethod;
	}
	
	public RequestHandler implement() {
		try {
			return doimplement();
		}
		catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	protected RequestHandler doimplement() throws Exception {
		ClassPool pool = ClassPool.getDefault();
		String implClassName = getImplClassName();
		CtClass implClass = pool.makeClass(implClassName);
		EzyClass superClass = new EzyClass(getSuperClass());
		String controllerFieldContent = makeControllerFieldContent();
		String setControllerMethodContent = makeSetControllerMethodContent();
		String handleRequestMethodContent = makeHandleRequestMethodContent();
		String handleExceptionMethodContent = makeHandleExceptionMethodContent();
		printComponentContent(controllerFieldContent);
		printComponentContent(setControllerMethodContent);
		printComponentContent(handleRequestMethodContent);
		printComponentContent(handleExceptionMethodContent);
		implClass.setSuperclass(pool.get(superClass.getName()));
		implClass.addField(CtField.make(controllerFieldContent, implClass));
		implClass.addMethod(CtNewMethod.make(setControllerMethodContent, implClass));
		implClass.addMethod(CtNewMethod.make(handleRequestMethodContent, implClass));
		implClass.addMethod(CtNewMethod.make(handleExceptionMethodContent, implClass));
		Class answerClass = implClass.toClass();
		implClass.detach();
		RequestHandler handler = (RequestHandler) answerClass.newInstance();
		setRepoComponent(handler);
		return handler;
	}
	
	protected void setRepoComponent(RequestHandler handler) {
		handler.setController(controller.getInstance());
	}
	
	protected String makeControllerFieldContent() {
		return new EzyInstruction()
				.append("private ")
					.append(controller.getClazz().getName())
						.append(" controller")
				.toString();
	}
	
	protected String makeSetControllerMethodContent() {
		return new EzyFunction(getSetControllerMethod())
				.body()
					.append(new EzyInstruction("\t", "\n")
							.append("this.controller")
							.equal()
							.cast(controller.getClazz().getClazz(), "arg0"))
					.function()
				.toString();
	}
	
	protected String makeHandleRequestMethodContent() {
		EzyBody body = new EzyFunction(getHandleRequestMethod())
				.body();
		int paramCount = 0;
		int parameterCount = 0;
		Parameter[] parameters = handlerMethod.getParameters();
		for(Parameter parameter : parameters) {
			EzyInstruction instruction = new EzyInstruction("\t", "\n");
			RequestParam requestParamAnno = parameter.getAnnotation(RequestParam.class);
			if(requestParamAnno != null) {
				instruction.clazz(parameter.getType())
					.append(" ").append(PARAMETER_PREFIX).append(paramCount)
					.equal();
				instruction
					.append("(java.lang.String)this.deserializeParameter(")
						.append("arg0.getParameter(").append(parameterCount).append(")")
						.append(", ").clazz(parameter.getType(), true)
					.append(")");
				++ parameterCount;
			}
			body.append(instruction);
			++ paramCount;
			
		}
		EzyInstruction instruction = new EzyInstruction("\t", "\n")
				.answer()
				.append("this.controller.").append(handlerMethod.getName())
				.append("(");
		for(int i = 0 ; i < paramCount ; ++i) {
			instruction.append(PARAMETER_PREFIX).append(i);
			if(i < paramCount - 1)
				instruction.append(", ");
		}
		instruction.append(")");
		return body.append(instruction).function().toString();
	}
	
	protected String makeHandleExceptionMethodContent() {
		EzyMethod method = getHandleExceptionMethod();
		EzyFunction function = new EzyFunction(method);
		EzyBody body = function.body();
		body.append(new EzyInstruction("\t", "\n")
				.answer()
				.string("hello"));
		return new StringBuilder()
				.append(method.getDeclaration(EzyReflections.MODIFIER_PUBLIC))
				.append(" throws Exception {\n")
				.append(body)
				.append("}")
				.toString();
	}
	
	protected EzyMethod getSetControllerMethod() {
		Method method = EzyMethods.getMethod(
				AbstractRequestHandler.class, "setController", Object.class);
		return new EzyMethod(method);
	}
	
	protected EzyMethod getHandleRequestMethod() {
		Method method = EzyMethods.getMethod(
				AbstractRequestHandler.class, "handleRequest", RequestArguments.class);
		return new EzyMethod(method);
	}
	
	protected EzyMethod getHandleExceptionMethod() {
		Method method = EzyMethods.getMethod(
				AbstractRequestHandler.class, "handleException", Exception.class);
		return new EzyMethod(method);
	}
	
	protected Class<?> getSuperClass() {
		return AbstractRequestHandler.class;
	}
	
	protected String getImplClassName() {
		return controller.getControllerName()
				+ "$" + handlerMethod.getName() + "$AutoImpl$" + COUNT.incrementAndGet();
	}
	
	private void printComponentContent(String componentContent) {
		if(debug) 
			logger.debug("reader: method content \n{}", componentContent);
	}
	
}
