package com.tvd12.ezyhttp.server.boot.test.interceptor;

import java.lang.reflect.Method;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.core.annotation.Interceptor;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

@Interceptor(priority = -1)
public class LogInterceptor
        extends EzyLoggable
        implements RequestInterceptor {

    @Override
    public boolean preHandle(
            RequestArguments arguments, 
            Method handler) {
        logger.info("pre handle request uri: {}, method: {}, handler: {}", 
                arguments.getRequest().getRequestURI(),
                arguments.getMethod(),
                handler);
        return true;
    }
    
    @Override
    public void postHandle(RequestArguments arguments, Method handler) {
        logger.info("post handle request uri: {}, method: {}, code: {}", 
                arguments.getRequest().getRequestURI(),
                arguments.getMethod(),
                arguments.getResponse().getStatus());
    }
    
    
}
