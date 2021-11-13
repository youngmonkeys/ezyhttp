package com.tvd12.ezyhttp.server.core.exception;

public class MaxUploadSizeException extends RuntimeException {
    private static final long serialVersionUID = 1156494852529775710L;

    public MaxUploadSizeException(long maxUploadSize) {
        super("Get max upload size: " + maxUploadSize);
    }
}
