package com.tvd12.ezyhttp.client.exception;

public class UploadCancelledException extends RuntimeException {
    private static final long serialVersionUID = -7594810062788758274L;

    public UploadCancelledException(String filePath) {
        super("Upload file: " + filePath + " is cancelled");
    }
}
