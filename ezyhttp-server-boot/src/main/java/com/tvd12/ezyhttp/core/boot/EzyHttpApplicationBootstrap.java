package com.tvd12.ezyhttp.core.boot;

import com.tvd12.ezyhttp.server.core.EzyHttpApplication;
import com.tvd12.ezyhttp.server.jetty.JettyApplicationBootstrap;

public final class EzyHttpApplicationBootstrap {

    private EzyHttpApplicationBootstrap() {}

    public static EzyHttpApplication start(Class<?> entryClass) throws Exception {
        return EzyHttpApplication.start(entryClass, JettyApplicationBootstrap.class);
    }

}
