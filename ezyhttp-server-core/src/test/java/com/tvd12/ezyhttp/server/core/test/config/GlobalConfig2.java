package com.tvd12.ezyhttp.server.core.test.config;

import com.tvd12.ezyfox.annotation.EzyPackagesToScan;
import com.tvd12.ezyhttp.server.core.annotation.ComponentsScan;
import com.tvd12.ezyhttp.server.core.annotation.PropertiesSources;

@ComponentsScan
@EzyPackagesToScan
@PropertiesSources("application2.yaml")
public class GlobalConfig2 { }
