package com.tvd12.ezyhttp.server.boot;

import com.tvd12.ezyhttp.server.core.EzyHttpApplication;

public final class EzyHttpApplicationBootstrap {

    private EzyHttpApplicationBootstrap() {}

    public static EzyHttpApplication start(
        Class<?> entryClass
    ) throws Exception {
        return EzyHttpApplication.start(entryClass);
    }
}
