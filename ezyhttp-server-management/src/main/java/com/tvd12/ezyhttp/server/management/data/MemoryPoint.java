package com.tvd12.ezyhttp.server.management.data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemoryPoint {

    private long maxMemory;
    private long freeMemory;
    private long totalMemory;

    public long getAllocatedMemory() {
        return totalMemory;
    }

    public long getUsedMemory() {
        return totalMemory - freeMemory;
    }
}
