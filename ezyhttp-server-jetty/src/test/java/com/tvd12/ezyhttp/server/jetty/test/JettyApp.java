package com.tvd12.ezyhttp.server.jetty.test;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyhttp.server.core.EzyHttpApplication;
import com.tvd12.ezyhttp.server.core.annotation.ComponentClasses;
import com.tvd12.ezyhttp.server.jetty.JettyApplicationBootstrap;

@ComponentClasses(JettyApplicationBootstrap.class)
public class JettyApp {
	
	public static void main(String[] args) throws Exception {
		EzyHttpApplication.start(JettyApp.class);
	}
	
	@Test
	public void testEnable() throws Exception {
		// given
		System.setProperty(EzyBeanContext.ACTIVE_PROFILES_KEY, "enable");
		
		// when
		EzyHttpApplication app = EzyHttpApplication.start(JettyApp.class);
		
		// then
		app.stop();
	}
	
	@Test
	public void testDisable() throws Exception {
		// given
		System.setProperty(EzyBeanContext.ACTIVE_PROFILES_KEY, "disable");
		
		// when
		EzyHttpApplication app = EzyHttpApplication.start(JettyApp.class);
		
		// then
		app.stop();
	}
}
