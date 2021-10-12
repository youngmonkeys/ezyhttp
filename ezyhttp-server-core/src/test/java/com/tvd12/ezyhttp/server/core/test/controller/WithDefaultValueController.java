package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.RequestCookie;
import com.tvd12.ezyhttp.server.core.annotation.RequestHeader;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;

@Controller("/api/v019")
public class WithDefaultValueController {

    
    @DoGet("/home")
    public Object home(
            @RequestParam(name = "param", defaultValue = "1") byte paramValue,
            @RequestHeader(name = "header", defaultValue = "2") int headerValue,
            @RequestCookie(name = "cookie", defaultValue = "3") String cookieValue) {
        return paramValue + headerValue + cookieValue;
    }
}
