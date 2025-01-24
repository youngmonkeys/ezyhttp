package com.tvd12.ezyhttp.client.concurrent;

public class UploadCancellationToken extends CancellationToken {

    public static final UploadCancellationToken ALWAYS_RUN =
        new UploadCancellationToken() {
            @Override
            public void cancel() {}
        };
}
