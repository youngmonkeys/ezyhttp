package com.tvd12.ezyhttp.core.exception;

public class MaxResourceUploadCapacity extends IllegalStateException {
    private static final long serialVersionUID = -3720935602307338922L;

    public MaxResourceUploadCapacity(int capacity) {
        super("max resource upload capacity: " + capacity);
    }
}
