package com.tvd12.ezyhttp.server.management;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;

@Controller
public class MetricsController {

    private final long startTime = System.currentTimeMillis();
    
    @DoGet("/management/start-time")
    public long getStartTime() {
        return startTime;
    }
    
    @DoGet("/management/live-time")
    public long getLiveTime() {
        long current = System.currentTimeMillis();
        return (current - startTime) / 1000;
    }
}
