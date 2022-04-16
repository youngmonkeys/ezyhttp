package com.tvd12.ezyhttp.core.boot.test.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tvd12.ezyfox.bean.annotation.EzyPostInit;
import com.tvd12.ezyhttp.core.boot.test.data.Customer;
import com.tvd12.ezyhttp.server.core.annotation.Service;

@Service
public class CustomerService {

	protected final Map<Long, Customer> customerById = new HashMap<>();
	protected final Map<String, Customer> customerByName = new HashMap<>();
	
	@EzyPostInit
	public void postInit() {
		save(new Customer(1L, "Hello", 25));
		save(new Customer(2L, "World", 26));
	}
	
	public Customer getCustomer(String name) {
		return customerByName.get(name);
	}
	
	public void save(Customer customer) {
		this.customerById.put(customer.getId(), customer);
		this.customerByName.put(customer.getName(), customer);
	}
	
	public List<Customer> getCustomersById(List<Long> ids) {
		return ids.stream()
				.filter(it -> customerById.containsKey(it))
				.map(it -> customerById.get(it))
				.collect(Collectors.toList());
	}
	
	public List<Customer> getCustomersByName(List<String> ids) {
		return ids.stream()
				.filter(it -> customerByName.containsKey(it))
				.map(it -> customerByName.get(it))
				.collect(Collectors.toList());
	}
}
