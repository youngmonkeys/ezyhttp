package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyfox.annotation.EzyFeature;
import com.tvd12.ezyfox.annotation.EzyPayment;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;

@EzyPayment
@EzyFeature("order")
@Controller("/api/v1.2.2")
public class PaymentController {

    @DoGet("/get-something")
    public void getSomeThing() {}
    
    @EzyPayment
    @DoGet("/get-and-buy-something")
    public void getAndBuySomeThing() {}
}
