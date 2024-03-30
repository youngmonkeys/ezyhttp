# ezyhttp <img src="https://github.com/youngmonkeys/ezyhttp/blob/master/logo.png" width="72" />

# Documentation

[https://youngmonkeys.org/projects/ezyhttp](https://youngmonkeys.org/projects/ezyhttp)

# Synopsis

EzyHttp is a library in EzyFox ecosystem, It supports to interact to HTTP (both http server and http client)

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
        return customers.get(name);
    }

    public void save(Customer customer) {
        this.customers.put(customer.getName(), customer);
    }
}
```

# License

- Apache License, Version 2.0

# Support Us

[Make a Meaningful Donation](https://youngmonkeys.org/donate/)

Currently, our operating budget is fully supported by our own salaries, and all product development is still based on voluntary contributions from a few organization members. The low budget is causing significant difficulties for us. Therefore, with a clear roadmap and an ambitious goal to provide intellectual products for the community, we would greatly appreciate your support in the form of a donation to help us take further steps. Thank you in advance for your meaningful contributions!

# Contact Us

- Get in touch with us on [Facebook](https://www.facebook.com/youngmonkeys.org)
- Ask us on [stackask.com](https://stackask.com)
- Email us at [contact@youngmonkeys.org](mailto:contact@youngmonkeys.org)
- Chat with us on [Discord](https://discord.gg/hKV2cbaT5h)
