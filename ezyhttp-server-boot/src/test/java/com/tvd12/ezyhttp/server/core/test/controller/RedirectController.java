package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.view.Redirect;

@Controller("/redirect")
public class RedirectController {
	
	@DoGet("/to-google")
	public Redirect toGoogle() {
		return Redirect.to("https://google.com");
	}
	
	@DoGet("/to-love")
	public Redirect toLove() {
		return Redirect.to("/love");
	}
	
	@DoGet("/to-home1")
	public Redirect toHome() {
		return Redirect.to("?firstName=Dzung&who=Dzung");
	}
	
}
