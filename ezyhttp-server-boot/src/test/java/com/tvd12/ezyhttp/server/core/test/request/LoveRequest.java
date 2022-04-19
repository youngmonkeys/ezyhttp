package com.tvd12.ezyhttp.server.core.test.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoveRequest {

    protected String who;
    protected int age;

}
