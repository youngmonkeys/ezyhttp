package com.tvd12.ezyhttp.server.management.monitor;

import com.tvd12.ezyfox.monitor.EzyMonitor;

public class SystemMonitor extends EzyMonitor {
	
	public static final SystemMonitor INSTANCE = new SystemMonitor(); 
	
	private SystemMonitor() {}
	
	public static SystemMonitor getInstance() {
		return INSTANCE;
	}
}
