package com.tvd12.ezyhttp.server.management.monitor;

import java.util.concurrent.atomic.AtomicLong;

import com.tvd12.ezyfox.monitor.EzyMonitor;

public class SystemMonitor extends EzyMonitor {
	
    private final AtomicLong requestCount;
    private EzyActionFrame requestPerSecond;
    private EzyActionFrame reponsePerSecond;
    
	public static final SystemMonitor INSTANCE = new SystemMonitor(); 
	
	private SystemMonitor() {
	    this.requestCount = new AtomicLong();
	    this.requestPerSecond = new EzyActionFrameSecond();
	    this.reponsePerSecond = new EzyActionFrameSecond();
	}
	
	public static SystemMonitor getInstance() {
		return INSTANCE;
	}
	
	public void increaseRequestCount() {
	    requestCount.incrementAndGet();
	    if (requestPerSecond.isExpired()) {
	        requestPerSecond = new EzyActionFrameSecond();
        }
	    requestPerSecond.addActions(1);
	}
	
	public long getRequestCount() {
	    return requestCount.get();
	}
	
	public long getRequestPerSecond() {
	    return requestPerSecond.getActions();
	}
	
	public void increaseResponseCount() {
        if (reponsePerSecond.isExpired()) {
            reponsePerSecond = new EzyActionFrameSecond();
        }
        reponsePerSecond.addActions(1);
    }
	
	public long getResponsePerSecond() {
        return reponsePerSecond.getActions();
    }
}
