package com.tvd12.ezyhttp.server.tomcat.test.controller;

import com.tvd12.ezyhttp.server.core.annotation.*;
import com.tvd12.ezyhttp.server.tomcat.test.annotation.NickName;
import com.tvd12.ezyhttp.server.tomcat.test.request.HelloRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        if (who == null)
            throw new IllegalArgumentException("who cannot be null");
        return "welcome " + who + " " + body.getWho();
    }

    @DoPost
    public String hello(
        @RequestBody HelloRequest body) {
        return "hello " + body.getWho();
    }

    @DoPut
    public void doNothing() {}

    @TryCatch({IllegalStateException.class, NullPointerException.class})
    public String handleException2(Exception e) {
        return e.getMessage();
    }

    @TryCatch(UnsupportedOperationException.class)
    public void handleException3(Exception e) {}
}
