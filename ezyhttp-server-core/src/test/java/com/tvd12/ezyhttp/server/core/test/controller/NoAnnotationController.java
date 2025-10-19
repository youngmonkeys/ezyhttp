package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.handler.IRequestController;

public class NoAnnotationController implements IRequestController {

    @DoGet("/no-annotation")
    public ResponseEntity noAnnotationGet() {
        return ResponseEntity.noContent();
    }
}
