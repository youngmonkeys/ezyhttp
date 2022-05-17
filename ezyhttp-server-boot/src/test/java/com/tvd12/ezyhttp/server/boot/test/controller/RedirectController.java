package com.tvd12.ezyhttp.server.boot.test.controller;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
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
        return Redirect.to("/?firstName=Dzung&who=Dzung");
    }

    @DoGet("/path1/hello")
    public Redirect path1HelloGet() {
        return Redirect.builder()
            .uri("/redirect/path2/world")
            .addAttribute("foo", "bar")
            .build();
    }

    @DoGet("/path2/world")
    public Object path2WorldGet(
        RequestArguments arguments
    ) {
        System.out.println(
            "redirect attribute: " +
                arguments.getRedirectionAttribute("foo")
        );
        return "Hello World";
    }
}
