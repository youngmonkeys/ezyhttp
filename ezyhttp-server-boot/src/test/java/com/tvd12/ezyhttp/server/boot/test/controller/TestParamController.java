package com.tvd12.ezyhttp.server.boot.test.controller;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.PathVariable;
import com.tvd12.ezyhttp.server.core.annotation.RequestHeader;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;

@Controller("/test")
public class TestParamController {

    @DoGet("/header")
    public boolean testHeader(@RequestHeader int header) {
        return true;
    }

    @DoGet("/header2")
    public boolean testHeader2(@RequestHeader("header") int header) {
        return true;
    }

    @DoGet("/parameter")
    public boolean testParameter(@RequestParam int param) {
        return true;
    }

    @DoGet("/parameter2")
    public boolean testParameter2(@RequestParam("param") int param) {
        return true;
    }

    @DoGet("/pathVariable/{var}")
    public boolean testPathVariable(@PathVariable int variable) {
        return true;
    }

    @DoGet("/pathVariable2/{var}")
    public boolean testPathVariable2(@PathVariable("variable") int variable) {
        return true;
    }

}
