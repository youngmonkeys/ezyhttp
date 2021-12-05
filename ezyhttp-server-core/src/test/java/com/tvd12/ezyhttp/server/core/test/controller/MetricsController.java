package com.tvd12.ezyhttp.server.core.test.controller;

import java.util.Collections;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.handler.ManagementController;

@Controller
public class MetricsController implements ManagementController {

    @DoGet("/management/metrics")
    public Object managementMetricsGet() {
        return Collections.singletonMap("hello", "world");
    }
}
