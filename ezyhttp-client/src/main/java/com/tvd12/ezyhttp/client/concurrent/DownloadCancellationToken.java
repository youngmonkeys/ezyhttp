package com.tvd12.ezyhttp.client.concurrent;

public class DownloadCancellationToken extends CancellationToken {

    public static final DownloadCancellationToken ALWAYS_RUN =
        new DownloadCancellationToken() {
            @Override
            public void cancel() {}
        };
}
