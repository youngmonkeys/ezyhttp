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
import com.tvd12.ezyhttp.server.core.annotation.RequestBody;
import com.tvd12.ezyhttp.server.core.annotation.RequestHeader;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;
import com.tvd12.ezyhttp.server.core.reflect.RequestParameters;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.util.RequestHeaderAnnotations;
import com.tvd12.ezyhttp.server.core.util.RequestParamAnnotations;

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
		String getResponseContentTypeMethodContent = makeGetResponseContentTypeMethodContent();
		printComponentContent(controllerFieldContent);
		printComponentContent(setControllerMethodContent);
		printComponentContent(handleRequestMethodContent);
		printComponentContent(handleExceptionMethodContent);
		printComponentContent(getResponseContentTypeMethodContent);
		implClass.setSuperclass(pool.get(superClass.getName()));
		implClass.addField(CtField.make(controllerFieldContent, implClass));
		implClass.addMethod(CtNewMethod.make(setControllerMethodContent, implClass));
		implClass.addMethod(CtNewMethod.make(handleRequestMethodContent, implClass));
		implClass.addMethod(CtNewMethod.make(handleExceptionMethodContent, implClass));
		implClass.addMethod(CtNewMethod.make(getResponseContentTypeMethodContent, implClass));
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
							.brackets(controller.getClazz().getClazz())
							.append("arg0"))
					.function()
				.toString();
	}
	
	protected String makeHandleRequestMethodContent() {
		EzyMethod method = getHandleRequestMethod();
		EzyFunction function = new EzyFunction(method);
		EzyBody body = function.body();
		int paramCount = 0;
		int headerCount = 0;
		int parameterCount = 0;
		Parameter[] parameters = handlerMethod.getParameters();
		for(Parameter parameter : parameters) {
			Class<?> parameterType = parameter.getType();
			EzyInstruction instruction = new EzyInstruction("\t", "\n")
					.clazz(parameterType)
					.append(" ").append(PARAMETER_PREFIX).append(paramCount)
					.equal();
			boolean hasAnnotation = false;
			RequestParam requestParamAnno = parameter.getAnnotation(RequestParam.class);
			if(requestParamAnno != null) {
				String paramKey = RequestParamAnnotations
						.getParamKeyString(requestParamAnno, parameterCount);
				instruction
					.append("(java.lang.String)this.deserializeParameter(")
						.append("arg0.getParameter(").append(paramKey).append(")")
						.append(", ").clazz(parameterType, true)
					.append(")");
				++ parameterCount;
				hasAnnotation = true;
			}
			RequestHeader requestHeaderAnno = parameter.getAnnotation(RequestHeader.class);
			if(requestHeaderAnno != null) {
				String headerKey = RequestHeaderAnnotations
						.getHeaderKeyString(requestHeaderAnno, headerCount);
				instruction
					.append("(java.lang.String)this.deserializeHeader(")
						.append("arg0.getHeader(").append(headerKey).append(")")
						.append(", ").clazz(parameterType, true)
					.append(")");
				++ headerCount;
				hasAnnotation = true;
			}
			RequestBody requestBodyAnno = parameter.getAnnotation(RequestBody.class);
			if(requestBodyAnno != null) {
				instruction
					.brackets(parameterType)
					.append("this.deserializeBody(")
						.append("arg0.getRequest(), ").clazz(parameterType, true)
					.append(")");
				hasAnnotation = true;
			}
			if(!hasAnnotation) {
				String argumentKey = RequestParameters.getArgumentKeyString(parameter);
				instruction
					.brackets(parameterType)
					.append("arg0.getArgument(").append(argumentKey)
				.append(")");
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
		body.append(instruction);
		return toThrowExceptionFunction(method, function);
	}
	
	protected String makeHandleExceptionMethodContent() {
		EzyMethod method = getHandleExceptionMethod();
		EzyFunction function = new EzyFunction(method);
		EzyBody body = function.body();
		body.append(new EzyInstruction("\t", "\n")
				.append("throw arg0"));
		return toThrowExceptionFunction(method, function);
	}
	
	protected String makeGetResponseContentTypeMethodContent() {
		return new EzyFunction(getGetResponseContentTypeMethod())
				.body()
					.append(new EzyInstruction("\t", "\n")
							.answer()
							.string(handlerMethod.getResponseType()))
					.function()
				.toString();
	}
	
	protected String toThrowExceptionFunction(EzyMethod method, EzyFunction function) {
		return new StringBuilder()
				.append(method.getDeclaration(EzyReflections.MODIFIER_PUBLIC))
				.append(" throws Exception {\n")
				.append(function.body())
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
	
	protected EzyMethod getGetResponseContentTypeMethod() {
		Method method = EzyMethods.getMethod(
				AbstractRequestHandler.class, "getResponseContentType");
		return new EzyMethod(method);
	}
	
	protected Class<?> getSuperClass() {
		return AbstractRequestHandler.class;
	}
	
	protected String getImplClassName() {
		return controller.getControllerName()
				+ "$" + handlerMethod.getName() + "$AutoImpl$" + COUNT.incrementAndGet();
	}
	
	protected void printComponentContent(String componentContent) {
		if(debug) 
			logger.debug("component content: \n{}", componentContent);
	}
	
}
