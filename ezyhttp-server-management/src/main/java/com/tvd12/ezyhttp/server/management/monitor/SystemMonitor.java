package com.tvd12.ezyhttp.server.management.monitor;

import com.tvd12.ezyfox.monitor.EzyMonitorIncludeRequestResponse;

public class SystemMonitor extends EzyMonitorIncludeRequestResponse {
	
	public static final SystemMonitor INSTANCE = new SystemMonitor(); 
	
	private SystemMonitor() {
	    super();
	}
	
	public static SystemMonitor getInstance() {
		return INSTANCE;
	}
}
