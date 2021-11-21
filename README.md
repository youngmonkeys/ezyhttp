# ezyhttp <img src="https://github.com/youngmonkeys/ezydata/blob/master/logo.png" width="72" />

# Documentation

[https://youngmonkeys.org/ezy-http](https://youngmonkeys.org/project/ezy-http/)

# Synopsis

EzyHttp is a library in EzyFox ecosystem, It support to interact to HTTP (both http server and http client)

# Code Example

For full example you can take a look [the examples repository](https://github.com/tvd12/ezyfox-examples)

**1. Start HTTP Server application**

```java
import com.tvd12.ezyhttp.core.boot.EzyHttpApplicationBootstrap;
import com.tvd12.ezyhttp.server.core.annotation.ComponentsScan;

@ComponentsScan({"packageA", "packageB"})
public class BootApp {

	public static void main(String[] args) throws Exception {
		EzyHttpApplicationBootstrap.start(BootApp.class);
	}
	
}
```

**2. Add a controller**

```java
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
```

**3. Add a service**

```java
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.core.boot.test.data.Customer;

@EzySingleton
public class CustomerService {

	protected final Map<String, Customer> customers = new HashMap<>();
	
	public Customer getCustomer(String name) {
		Customer customer = customers.get(name);
		return customer;
	}
	
	public void save(Customer customer) {
		this.customers.put(customer.getName(), customer);
	}
}
```

# Motivation

In an effort to bring HTTP to EzyFox ecosystem and help everyone study one do everything

# API Reference

[https://youngmonkeys.org/ezy-http](https://youngmonkeys.org/project/ezy-http/)

# Tests

mvn test

# Contributors

- [DungTV](mailto:itprono3@gmail.com)

# License

- Apache License, Version 2.0
