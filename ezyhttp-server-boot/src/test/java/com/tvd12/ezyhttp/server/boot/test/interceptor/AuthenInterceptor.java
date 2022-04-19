package com.tvd12.ezyhttp.server.boot.test.interceptor;

import java.lang.reflect.Method;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.core.annotation.Interceptor;
import com.tvd12.ezyhttp.core.exception.HttpUnauthorizedException;
import com.tvd12.ezyhttp.server.boot.test.constant.AuthenticatedMethods;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

@Interceptor(priority = -1)
public class AuthenInterceptor
    extends EzyLoggable
    implements RequestInterceptor {

    @Override
    public boolean preHandle(
        RequestArguments arguments,
        Method handler) {
        boolean mustAuthen = AuthenticatedMethods.AUTHENTICATED_METHODS.contains(handler);
        if (mustAuthen) {
            String token = arguments.getHeader("token");
            if (token == null) {
                throw new HttpUnauthorizedException("invalid token");
            }
        }
        return true;
    }
}
