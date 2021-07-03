package com.tvd12.ezyhttp.server.core.test.controller;

import java.util.Collections;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;
import com.tvd12.ezyhttp.server.core.view.View;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Controller("/view")
public class ViewController {

	@DoGet("/home")
	public View home() {
		return View.of("home");
	}
	
	@DoGet("/greet")
	public View home(@RequestParam String who) {
		return View.builder()
				.template("greet/greet")
				.addVariable("who", who)
				.addVariable("welcome", new Welcome("Welcome " + who))
				.addVariable("hi", Collections.singletonMap("message", "Hi " + who))
				.build();
	}
	
	@Getter
	@AllArgsConstructor
	public static class Welcome {
		private String message;
	}
}
