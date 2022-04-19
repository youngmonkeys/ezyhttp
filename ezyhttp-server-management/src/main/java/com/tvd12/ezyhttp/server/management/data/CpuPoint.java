package com.tvd12.ezyhttp.server.management.data;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CpuPoint {
    private double systemCpuLoad;
    private double processCpuLoad;
    private double processGcActivity;
}
