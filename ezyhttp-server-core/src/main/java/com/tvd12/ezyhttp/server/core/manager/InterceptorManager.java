package com.tvd12.ezyhttp.server.core.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;
import static com.tvd12.ezyhttp.server.core.util.InterceptorAnnotations.*;

import lombok.Getter;

public class InterceptorManager implements EzyDestroyable {

	@Getter
	public final List<RequestInterceptor> requestInterceptors;
	
	public InterceptorManager() {
		this.requestInterceptors = new ArrayList<>();
	}
	
	public void addRequestInterceptors(List<RequestInterceptor> interceptors) {
		this.requestInterceptors.addAll(interceptors);
		this.requestInterceptors.sort(requestInterceptorComparator());
	}
	
	protected Comparator<RequestInterceptor> requestInterceptorComparator() {
		return (a, b) -> getPriority(a) - getPriority(b);
	}
	
	@Override
	public void destroy() {
		this.requestInterceptors.clear();
	}
}
