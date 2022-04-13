package com.tvd12.ezyhttp.client.exception;

public class DownloadCancelledException extends RuntimeException {

    public DownloadCancelledException(String fileURL) {
        super("Downloading file: " + fileURL + " is cancelled");
    }
}
