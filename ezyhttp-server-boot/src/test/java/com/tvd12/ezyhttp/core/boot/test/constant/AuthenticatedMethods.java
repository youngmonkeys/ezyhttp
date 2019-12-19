package com.tvd12.ezyhttp.core.boot.test.constant;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.tvd12.ezyfox.reflect.EzyMethods;
import com.tvd12.ezyhttp.server.core.test.controller.CustomerController;

public final class AuthenticatedMethods {

	public static Set<Method> AUTHENTICATED_METHODS = authenticatedMethods();
	
	private AuthenticatedMethods() {}
	
	private static Set<Method> authenticatedMethods() {
		Set<Method> methods = new HashSet<>();
		methods.addAll(EzyMethods.getDeclaredMethods(CustomerController.class));
		return Collections.unmodifiableSet(methods);
	}
	
}
