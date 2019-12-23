package com.tvd12.ezyhttp.server.core.test.controller;

import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyhttp.core.boot.test.data.Customer;
import com.tvd12.ezyhttp.core.boot.test.service.CustomerService;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;
import com.tvd12.ezyhttp.core.exception.HttpNotFoundException;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.DoPost;
import com.tvd12.ezyhttp.server.core.annotation.PathVariable;
import com.tvd12.ezyhttp.server.core.annotation.RequestBody;

import lombok.Setter;

@Setter
@Controller("/api/v1/customer")
public class CustomerController {

	@EzyAutoBind
	protected CustomerService customerService;
	
	@DoGet("/{zone}/{name}")
	public Customer getCustomer(@PathVariable("name") String name) {
		Customer customer = customerService.getCustomer(name);
		if(customer == null)
			throw new HttpNotFoundException("customer: " + name + " not found");
		return customer;
	}
	
	@DoPost("/add")
	public ResponseEntity addCustomer(@RequestBody Customer customer) {
		validateCustomer(customer);
		customerService.save(customer);
		return ResponseEntity.status(StatusCodes.NO_CONTENT).build();
	}
	
	protected void validateCustomer(Customer customer) {
		Map<String, String> errors = new HashMap<>();
		if(customer == null) {
			errors.put("customer", "required");
		}
		else {
			if(customer.getName() == null)
				errors.put("name", "required");
			if(customer.getAge() < 1)
				errors.put("age", "invalid");
		}
		if(errors.size() > 0)
			throw new HttpBadRequestException(errors);
	}
	
}
