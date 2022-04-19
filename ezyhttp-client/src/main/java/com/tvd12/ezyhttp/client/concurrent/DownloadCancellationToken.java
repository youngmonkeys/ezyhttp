package com.tvd12.ezyhttp.client.concurrent;

public class DownloadCancellationToken {

    private volatile boolean cancelled;

    public static final DownloadCancellationToken ALWAYS_RUN =
        new DownloadCancellationToken() {
            @Override
            public void cancel() {}
        };

    public void cancel() {
        synchronized (this) {
            this.cancelled = true;
        }
    }

    public boolean isCancelled() {
        synchronized (this) {
            return cancelled;
        }
    }
}
