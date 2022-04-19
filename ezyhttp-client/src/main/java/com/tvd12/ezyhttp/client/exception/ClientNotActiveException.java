package com.tvd12.ezyhttp.client.exception;

public class ClientNotActiveException extends RuntimeException {
    private static final long serialVersionUID = 5701056109879520472L;

    public ClientNotActiveException() {
        super("client has not actived or has stopped");
    }

}
