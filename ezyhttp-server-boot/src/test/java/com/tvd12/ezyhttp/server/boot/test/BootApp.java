package com.tvd12.ezyhttp.server.boot.test;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.boot.EzyHttpApplicationBootstrap;
import com.tvd12.ezyhttp.server.core.annotation.ComponentsScan;
import com.tvd12.ezyhttp.server.core.asm.ExceptionHandlerImplementer;
import com.tvd12.ezyhttp.server.core.asm.RequestHandlerImplementer;

@ComponentsScan("com.tvd12.ezyhttp.server.boot.test")
public class BootApp {

    public static void main(String[] args) throws Exception {
        RequestHandlerImplementer.setDebug(true);
        ExceptionHandlerImplementer.setDebug(true);
        EzyHttpApplicationBootstrap.start(BootApp.class);
    }
    
    @Test
    public void test() throws Exception {
        EzyHttpApplicationBootstrap.start(BootApp.class);
    }
}
