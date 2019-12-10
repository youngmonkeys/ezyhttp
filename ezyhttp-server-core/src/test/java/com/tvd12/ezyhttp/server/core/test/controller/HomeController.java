package com.tvd12.ezyhttp.server.core.test.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.DoPost;
import com.tvd12.ezyhttp.server.core.annotation.RequestArgument;
import com.tvd12.ezyhttp.server.core.annotation.RequestBody;
import com.tvd12.ezyhttp.server.core.annotation.RequestHeader;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;
import com.tvd12.ezyhttp.server.core.test.annotation.NickName;
import com.tvd12.ezyhttp.server.core.test.request.HelloRequest;

@Controller("/")
public class HomeController {

	@DoGet
	public String welcome(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("firstName") String firstName,
			@RequestParam String who,
			@RequestHeader("key") String key,
			@RequestHeader String token,
			@RequestArgument("name") String name,
			@RequestBody HelloRequest body,
			@NickName String nickName) {
		System.out.println("request uri: " + request.getRequestURI());
		return "welcome " + who + " " + body.getWho();
	}
	
	@DoPost
	public String hello(
			@RequestBody HelloRequest body) {
		return "hello " + body.getWho();
	}
	
}
