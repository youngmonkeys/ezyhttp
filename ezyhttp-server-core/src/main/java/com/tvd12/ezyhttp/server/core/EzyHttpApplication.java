package com.tvd12.ezyhttp.server.core;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.EzyStartable;
import com.tvd12.ezyhttp.server.core.annotation.ApplicationBootstrap;

import lombok.Getter;

public class EzyHttpApplication 
		extends EzyLoggable
		implements EzyStartable {

	@Getter
	protected final ApplicationContext applicationContext;
	
	public EzyHttpApplication(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Override
	public void start() throws Exception {
		ApplicationEntry entry = applicationContext.getSingleton(ApplicationBootstrap.class);
		if(entry == null)
			throw new IllegalStateException("Failed to start application, the ApplicationEntry not found");
		entry.start();
	}
	
}
