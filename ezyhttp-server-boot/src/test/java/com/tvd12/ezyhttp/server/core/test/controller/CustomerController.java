package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyhttp.core.boot.test.data.Customer;
import com.tvd12.ezyhttp.core.boot.test.service.CustomerService;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.DoPost;
import com.tvd12.ezyhttp.server.core.annotation.RequestBody;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;

import lombok.Setter;

@Setter
@Controller("/api/v1/customer")
public class CustomerController {

	@EzyAutoBind
	protected CustomerService customerService;
	
	@DoGet
	public Customer getCustomer(@RequestParam String name) {
		Customer customer = customerService.getCustomer(name);
		return customer;
	}
	
	@DoPost("/add")
	public ResponseEntity addCustomer(@RequestBody Customer customer) {
		customerService.save(customer);
		return ResponseEntity.status(StatusCodes.NO_CONTENT).build();
	}
	
}
