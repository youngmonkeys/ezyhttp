package com.tvd12.ezyhttp.server.core.servlet;

import com.tvd12.ezyhttp.core.constant.StatusCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public interface AsyncCallback extends AsyncListener {

    Logger LOGGER = LoggerFactory.getLogger(AsyncCallback.class);

    @Override
    default void onError(AsyncEvent event) {
        try {
            AsyncContext asyncContext = event.getAsyncContext();
            ServletResponse response = asyncContext.getResponse();
            if (response instanceof HttpServletResponse) {
                ((HttpServletResponse) response).setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
            }
            asyncContext.complete();
            LOGGER.info("AsyncCallback.onError, request = {}", event.getSuppliedRequest());
        } catch (Throwable e) {
            LOGGER.info("AsyncCallback.onError failed, request = {}", event.getSuppliedRequest());
        }
    }

    @Override
    default void onTimeout(AsyncEvent event) {
        try {
            AsyncContext asyncContext = event.getAsyncContext();
            ServletResponse response = asyncContext.getResponse();
            if (response instanceof HttpServletResponse) {
                ((HttpServletResponse) response).setStatus(StatusCodes.REQUEST_TIMEOUT);
            }
            asyncContext.complete();
            LOGGER.info("AsyncCallback.onTimeout, request = {}", event.getSuppliedRequest());
        } catch (Throwable e) {
            LOGGER.info("AsyncCallback.onTimeout failed, request = {}", event.getSuppliedRequest());
        }
    }

    @Override
    default void onStartAsync(AsyncEvent event) {
        LOGGER.info("AsyncCallback.onStartAsync, request = {}", event.getSuppliedRequest());
    }
}
