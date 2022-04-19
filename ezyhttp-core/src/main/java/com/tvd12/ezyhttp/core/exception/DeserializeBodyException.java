package com.tvd12.ezyhttp.core.exception;

import java.io.IOException;

public class DeserializeBodyException extends IOException {
    private static final long serialVersionUID = 351983342119059608L;

    public DeserializeBodyException(String msg, Exception e) {
        super(msg, e);
    }
    
}
