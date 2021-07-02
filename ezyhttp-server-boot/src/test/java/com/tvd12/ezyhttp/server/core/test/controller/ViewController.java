package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.view.View;

@Controller("/view")
public class ViewController {

	@DoGet("/home")
	public View home() {
		return View.of("home");
	}
	
}
