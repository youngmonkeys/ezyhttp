package com.tvd12.ezyhttp.server.management.data;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ThreadCountPoint {

    private int threadCount;
    private int daemonThreadCount;
}
