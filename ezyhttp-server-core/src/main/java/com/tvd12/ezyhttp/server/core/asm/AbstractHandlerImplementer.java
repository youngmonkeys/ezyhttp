package com.tvd12.ezyhttp.server.core.asm;

import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyfox.asm.EzyInstruction;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.server.core.reflect.ExceptionHandlerMethod;
import com.tvd12.ezyhttp.server.core.reflect.HandlerMethod;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

public class AbstractHandlerImplementer<H extends HandlerMethod> 
		extends EzyLoggable {
	
	protected final H handlerMethod;
	
	public AbstractHandlerImplementer(H handlerMethod) {
		this.handlerMethod = handlerMethod;
	}
	
	protected void appendHandleExceptionMethodArguments(
			ExceptionHandlerMethod method,
			EzyInstruction instruction, 
			Class<?> exceptionClass) {
		int paramCount = 0;
		Parameter[] parameters = method.getParameters();
		for(Parameter parameter : parameters) {
			Class<?> parameterType = parameter.getType();
			if (parameterType == RequestArguments.class) {
				instruction.append("arg0");
			}
			else if (parameterType == HttpServletRequest.class) {
				instruction.append("arg0.getRequest()");
			}
			else if (parameterType == HttpServletResponse.class) {
				instruction.append("arg0.getResponse()");
			}
			else if (Throwable.class.isAssignableFrom(parameterType)) {
				instruction.brackets(exceptionClass).append("arg1");
			}
			else if (parameterType == boolean.class) {
				instruction.append("false");
			}
			else if (parameterType.isPrimitive()) {
				instruction.append("0");
			}
			else {
				instruction.append("null");
			}
			if ((paramCount ++) < (parameters.length - 1))
				instruction.append(", ");
		}
	}
	
}
