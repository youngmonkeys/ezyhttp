package com.tvd12.ezyhttp.server.core.test.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.annotation.ExceptionHandler;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

@ExceptionHandler
public class GlobalExceptionHandler {

    @TryCatch(IllegalArgumentException.class)
    public ResponseEntity handleException(Exception e) {
        return ResponseEntity.badRequest("global: " + e.getMessage());
    }

    @TryCatch({IllegalStateException.class, NullPointerException.class})
    public String handleException2(Exception e) {
        return e.getMessage();
    }

    @TryCatch(java.lang.ClassCastException.class)
    public void handleException3(Exception e) {}

    @TryCatch(java.lang.RuntimeException.class)
    public void handleException4(
        RequestArguments args,
        HttpServletRequest request,
        HttpServletResponse response,
        boolean booleanValue,
        int intValue,
        String stringValue,
        Exception e) {
    }

    @TryCatch(java.lang.Exception.class)
    public void handleException5() {}
}
