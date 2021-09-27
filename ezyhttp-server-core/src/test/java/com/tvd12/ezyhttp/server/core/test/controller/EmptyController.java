package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.PathVariable;

@Controller(uri = "empty")
public class EmptyController {

	@DoGet("/empty/{foo}/{bar}")
	public void doGet(@PathVariable String value) {}
}
