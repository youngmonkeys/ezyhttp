package com.tvd12.ezyhttp.server.jetty.test.limit;

import java.util.concurrent.atomic.AtomicInteger;

public final class LimitState {

    public static final AtomicInteger HANDLED_JSONS = new AtomicInteger();
    public static final AtomicInteger HANDLED_UPLOADS = new AtomicInteger();
    public static volatile String seenFolder;
    public static volatile String seenDescription;
    public static volatile String seenRawDescription;

    private LimitState() {}

    public static void reset() {
        HANDLED_JSONS.set(0);
        HANDLED_UPLOADS.set(0);
        seenFolder = null;
        seenDescription = null;
        seenRawDescription = null;
    }
}
