package com.tvd12.ezyhttp.server.core.test.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoDelete;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.DoPost;
import com.tvd12.ezyhttp.server.core.annotation.DoPut;
import com.tvd12.ezyhttp.server.core.annotation.PathVariable;
import com.tvd12.ezyhttp.server.core.annotation.RequestArgument;
import com.tvd12.ezyhttp.server.core.annotation.RequestBody;
import com.tvd12.ezyhttp.server.core.annotation.RequestHeader;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;
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
		if(who == null)
			throw new IllegalArgumentException("who cannot be null");
		return "welcome " + who + " " + body.getWho();
	}
	
	@DoPost
	public String hello(
			@RequestBody HelloRequest body) {
		return "hello " + body.getWho();
	}
	
	@DoPut
	public void doNothing() {
	}
	
	@DoGet("bye")
	public String bye(@RequestParam List<String> messages) {
		return "bye: " + messages;
	}
	
	@DoGet("see")
	public String see(
			@RequestParam List<String> messages, 
			@PathVariable("name") String name) {
		return "bye: " + messages;
	}
	
	@DoPut("see")
	public String put(
			@RequestParam List<String> messages, 
			@PathVariable("name") String name) {
		return "bye: " + messages;
	}
	
	@DoDelete("see")
	public String delete(
			@RequestHeader int count,
			@RequestParam int skip,
			@RequestParam int limit,
			@RequestParam List<String> messages, 
			@PathVariable("name") String name) {
		return "bye: " + messages;
	}
	
	@TryCatch({IllegalStateException.class, NullPointerException.class})
	public String handleException2(Exception e) {
		return e.getMessage();
	}
	
	@TryCatch(java.lang.UnsupportedOperationException.class)
	public void handleException3(Exception e) {
	}
	
}
