package com.tvd12.ezyhttp.core.boot.test;

import com.tvd12.ezyhttp.core.boot.EzyHttpApplicationBootstrap;
import com.tvd12.ezyhttp.server.core.annotation.ComponentsScan;

@ComponentsScan("com.tvd12.ezyhttp.server.core.test")
public class BootApp {

	public static void main(String[] args) throws Exception {
		EzyHttpApplicationBootstrap.start(BootApp.class);
	}
	
}
