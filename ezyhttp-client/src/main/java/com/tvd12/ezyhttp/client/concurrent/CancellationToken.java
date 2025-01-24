package com.tvd12.ezyhttp.client.concurrent;

public class CancellationToken {

    private volatile boolean cancelled;

    public static final CancellationToken ALWAYS_RUN =
        new CancellationToken() {
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
