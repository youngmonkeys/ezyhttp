package com.tvd12.ezyhttp.server.jetty.test.limit;

import com.tvd12.ezyhttp.core.annotation.Interceptor;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Interceptor
public class LimitInterceptor implements RequestInterceptor {

    @Override
    public boolean preHandle(RequestArguments arguments, Method handler) {
        HttpServletRequest request = arguments.getRequest();
        if (!"/limit/upload".equals(request.getRequestURI())) {
            return true;
        }
        LimitState.seenFolder = arguments.getParameter("folder");
        LimitState.seenDescription = arguments.getParameter("description");
        LimitState.seenRawDescription = request.getParameter("description");
        return request.getHeader("token") != null;
    }
}
