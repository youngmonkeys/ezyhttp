package com.tvd12.ezyhttp.stresstest;

import com.tvd12.ezyhttp.core.boot.EzyHttpApplicationBootstrap;

public class EzyHttpStresstest {
	
	public static void main(String[] args) throws Exception {
		EzyHttpApplicationBootstrap.start(EzyHttpStresstest.class);
	}
	
}
