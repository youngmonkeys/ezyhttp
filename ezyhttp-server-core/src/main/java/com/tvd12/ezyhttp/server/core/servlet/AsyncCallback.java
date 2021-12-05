package com.tvd12.ezyhttp.server.core.servlet;

import java.io.IOException;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface AsyncCallback extends AsyncListener {
    
    Logger LOGGER = LoggerFactory.getLogger(AsyncCallback.class);

    @Override
    default void onError(AsyncEvent event) throws IOException {
        LOGGER.info("AsyncCallback.onError, request = {}", event.getSuppliedRequest());
    }
    
    @Override
    default void onTimeout(AsyncEvent event) throws IOException {
        LOGGER.info("AsyncCallback.onTimeout, request = {}", event.getSuppliedRequest());
    }
    
    @Override
    default void onStartAsync(AsyncEvent event) throws IOException {
        LOGGER.info("AsyncCallback.onStartAsync, request = {}", event.getSuppliedRequest());
    }
}
