package com.tvd12.ezyhttp.server.core.reflect;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.reflect.EzyMethod;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;
import com.tvd12.ezyhttp.server.core.util.TryCatchAnnotations;

import lombok.Getter;

@Getter
public class ExceptionHandlerMethod {

	protected final EzyMethod method;
	protected final Class<?>[] exceptionClasses;
	
	public ExceptionHandlerMethod(EzyMethod method) {
		this.method = method;
		this.exceptionClasses = fetchExceptionClasses();
		
	}
	
	protected Class<?>[] fetchExceptionClasses() {
		TryCatch tryCatch = method.getAnnotation(TryCatch.class);
		Class<?>[] classes = TryCatchAnnotations.getExceptionClasses(tryCatch);
		return classes;
	}
	
	public String getName() {
		return method.getName();
	}
	
	public Class<?> getReturnType() {
		return method.getReturnType();
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(method.getName())
				.append("(")
					.append("exceptionClasses: ").append(EzyStrings.join(exceptionClasses, ", "))
				.append(")")
				.toString();
	}
	
}
