package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;

@Controller("/")
public class HomeController {

	@DoGet
	public String welcome(@RequestParam String who) {
		return "welcome " + who;
	}
	
}
