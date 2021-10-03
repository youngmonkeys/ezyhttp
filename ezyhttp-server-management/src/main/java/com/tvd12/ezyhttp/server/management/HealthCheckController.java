package com.tvd12.ezyhttp.server.management;

import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;

@Controller
public class HealthCheckController {
    
    @DoGet("/health-check")
    public ResponseEntity healthCheck() {
        return ResponseEntity.ok();
    }
    
}
