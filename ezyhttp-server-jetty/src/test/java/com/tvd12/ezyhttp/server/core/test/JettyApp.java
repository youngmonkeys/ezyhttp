package com.tvd12.ezyhttp.server.core.test;

import com.tvd12.ezyhttp.server.core.EzyHttpApplication;
import com.tvd12.ezyhttp.server.core.annotation.ComponentClasses;
import com.tvd12.ezyhttp.server.jetty.JettyApplicationBootstrap;

@ComponentClasses(JettyApplicationBootstrap.class)
public class JettyApp {
	
	public static void main(String[] args) throws Exception {
		EzyHttpApplication.start(JettyApp.class);
	}

}
