package com.tvd12.ezyhttp.core.constant;

import com.tvd12.ezyfox.constant.EzyConstant;

import lombok.Getter;

@Getter
public enum HttpMethod implements EzyConstant {

    GET(1, "get"),
    POST(2, "post"),
    PUT(3, "put"),
    DELETE(4, "delete"),
    PATCH(5, "patch"),
    HEAD(6, "head"),
    OPTIONS(7, "options"),
    TRACE(8, "trace");

    private final int id;
    private final String name;

    HttpMethod(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Check whether the method has a request body or not
     *
     * @return true if the method has a request body
     */
    public boolean hasOutput() {
        return this == HttpMethod.POST ||
            this == HttpMethod.PUT ||
            this == HttpMethod.PATCH;
    }

}
