package com.tvd12.ezyhttp.server.boot.test.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.boot.test.exception.NoPermissionException;
import com.tvd12.ezyhttp.server.core.annotation.ExceptionHandler;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

@ExceptionHandler
public class GlobalExceptionHandler {

    @TryCatch(IllegalArgumentException.class)
    public ResponseEntity handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest("global: " + e.getMessage());
    }
    
    @TryCatch({IllegalStateException.class, NullPointerException.class})
    public String handleException2(Exception e) {
        e.printStackTrace();
        return e.getMessage();
    }
    
    @TryCatch(java.lang.UnsupportedOperationException.class)
    public void handleException3(Exception e) {
        e.printStackTrace();
    }
    
    @TryCatch(InvalidFormatException.class)
    public void handleException(
            RequestArguments args,
            HttpServletRequest request,
            HttpServletResponse response,
            InvalidFormatException e) {
        e.printStackTrace();
        Map<String, String> data = new HashMap<>();
        for (Reference reference : e.getPath())
            data.put(reference.getFieldName(), "invalid");
        throw new HttpBadRequestException(data);
    }
    
    @TryCatch(NoPermissionException.class)
    public ResponseEntity handleException(NoPermissionException e) {
        return ResponseEntity.builder()
                .status(StatusCodes.UNAUTHORIZED)
                .body("has no permission")
                .build();
    }
    
}
