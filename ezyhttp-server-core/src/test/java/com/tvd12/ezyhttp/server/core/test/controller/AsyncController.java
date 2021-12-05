package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyhttp.server.core.annotation.Async;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;

@Controller
public class AsyncController {

    @Async
    @DoGet("/async/call1")
    public void asyncCall1() {
        
    }
}
