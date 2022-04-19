package com.tvd12.ezyhttp.server.boot.test.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    protected long id;
    protected String name;
    protected int age;

}
