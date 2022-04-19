package com.tvd12.ezyhttp.server.management;

import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.handler.ManagementController;

@Controller
public class HealthCheckController implements ManagementController {

    @DoGet("/health-check")
    public ResponseEntity healthCheck() {
        return ResponseEntity.ok();
    }

    @DoGet("/management/health-check")
    public ResponseEntity managementHealthCheck() {
        return ResponseEntity.noContent();
    }
}
