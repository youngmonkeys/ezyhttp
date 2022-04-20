package com.tvd12.ezyhttp.server.core.asm;

import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;

public interface AsmUncaughtExceptionHandler extends UncaughtExceptionHandler {

    default void setExceptionHandler(Object handler) {}
}
