package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyfox.annotation.EzyFeature;
import com.tvd12.ezyfox.annotation.EzyManagement;
import com.tvd12.ezyfox.annotation.EzyPayment;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;

@Controller("/api/v1.2.2")
public class PaymentMethodController {

    @EzyManagement
    @EzyFeature("order")
    @EzyPayment
    @DoGet("/buy-something")
    public void buySomeThing() {}
}
