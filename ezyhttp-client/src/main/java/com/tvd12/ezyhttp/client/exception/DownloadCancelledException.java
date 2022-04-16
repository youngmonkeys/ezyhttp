package com.tvd12.ezyhttp.client.exception;

public class DownloadCancelledException extends RuntimeException {
    private static final long serialVersionUID = -7594810062788758274L;

    public DownloadCancelledException(String fileURL) {
        super("Downloading file: " + fileURL + " is cancelled");
    }
}
