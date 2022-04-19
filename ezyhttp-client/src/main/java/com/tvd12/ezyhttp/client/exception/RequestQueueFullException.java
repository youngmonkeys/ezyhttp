package com.tvd12.ezyhttp.client.exception;

public class RequestQueueFullException extends RuntimeException {
    private static final long serialVersionUID = -6028459963739952661L;

    public RequestQueueFullException(int capacity) {
        super("request queue got max capacity: " + capacity);
    }

}
