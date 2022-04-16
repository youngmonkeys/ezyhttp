package com.tvd12.ezyhttp.server.core.reflect;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;
import com.tvd12.ezyhttp.server.core.util.TryCatchAnnotations;

import lombok.Getter;

@Getter
public class ExceptionHandlerMethod extends HandlerMethod {

	protected final String responseType;
	protected final Class<?>[] exceptionClasses;
	
	public ExceptionHandlerMethod(EzyMethod method) {
		super(method);
		this.responseType = fetchResponseType();
		this.exceptionClasses = fetchExceptionClasses();
		
	}
	
	protected Class<?>[] fetchExceptionClasses() {
		TryCatch annotation = method.getAnnotation(TryCatch.class);
        return TryCatchAnnotations.getExceptionClasses(annotation);
	}
	
	protected String fetchResponseType() {
		TryCatch annotation = method.getAnnotation(TryCatch.class);
		return TryCatchAnnotations.getResponseType(annotation);
	}
	
	@Override
	public String toString() {
		return method.getName() +
			"(" +
			"exceptionClasses: " + EzyStrings.join(exceptionClasses, ", ") +
			")";
	}
	
}
