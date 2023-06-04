package com.tvd12.ezyhttp.server.management.data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiskPoint {

    private long freeSpace;
    private long totalSpace;
    private long usableSpace;
}
