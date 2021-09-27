package com.tvd12.ezyhttp.server.core.test.config;

import com.tvd12.ezyhttp.server.core.annotation.ComponentClasses;
import com.tvd12.ezyhttp.server.core.annotation.ComponentsScan;
import com.tvd12.ezyhttp.server.core.test.event.EventService;

@ComponentClasses(EventService.class)
@ComponentsScan("com.tvd12.ezyhttp.server.core.test.event")
public class GlobalConfig {
}
