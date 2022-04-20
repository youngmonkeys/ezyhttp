package com.tvd12.ezyhttp.server.core.manager;

import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;
import com.tvd12.ezyhttp.server.core.util.InterceptorAnnotations;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        return Comparator.comparingInt(InterceptorAnnotations::getPriority);
    }

    @Override
    public void destroy() {
        this.requestInterceptors.clear();
    }
}
