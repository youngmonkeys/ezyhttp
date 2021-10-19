package com.tvd12.ezyhttp.server.management;

import com.tvd12.ezyfox.monitor.EzyCpuMonitor;
import com.tvd12.ezyfox.monitor.EzyGcMonitor;
import com.tvd12.ezyfox.monitor.EzyMemoryMonitor;
import com.tvd12.ezyfox.monitor.EzyThreadsMonitor;
import com.tvd12.ezyfox.monitor.data.EzyThreadsDetail;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.handler.ManagementController;
import com.tvd12.ezyhttp.server.management.data.CpuPoint;
import com.tvd12.ezyhttp.server.management.data.MemoryPoint;
import com.tvd12.ezyhttp.server.management.data.ThreadCountPoint;
import com.tvd12.ezyhttp.server.management.monitor.SystemMonitor;

@Controller
public class MetricsController implements ManagementController {

    private final long startTime = System.currentTimeMillis();
    
    @DoGet("/management/start-time")
    public long getStartTime() {
        return startTime;
    }
    
    @DoGet("/management/live-time")
    public long getLiveTime() {
        long current = System.currentTimeMillis();
        return (current - startTime) / 1000;
    }
    
    @DoGet("/management/active-threads")
    public EzyThreadsDetail activeThreadsGet() {
        return SystemMonitor.getInstance()
            .getThreadsMonitor()
            .getThreadsDetails();
    }
    
    @DoGet("/management/thread-count")
    public ThreadCountPoint threadsGet() {
        EzyThreadsMonitor threadsMonitor = SystemMonitor.getInstance()
            .getThreadsMonitor();
        int threadCount = threadsMonitor.getThreadCount();
        int daemonThreadCount = threadsMonitor.getDaemonThreadCount();
        return ThreadCountPoint.builder()
                .threadCount(threadCount)
                .daemonThreadCount(daemonThreadCount)
                .build();
    }

    @DoGet("/management/cpu-usage")
    public CpuPoint cpuUsageGet() {
        SystemMonitor monitor = SystemMonitor.getInstance();
        EzyGcMonitor gcMonitor = monitor.getGcMonitor();
        EzyCpuMonitor cpuMonitor = monitor.getCpuMonitor();
        return CpuPoint.builder()
            .systemCpuLoad(cpuMonitor.getSystemCpuLoad())
            .processCpuLoad(cpuMonitor.getProcessCpuLoad())
            .processGcActivity(gcMonitor.getProcessGcActivity())
            .build();
    }

    @DoGet("/management/memory-usage")
    public MemoryPoint memoryUsageGet() {
        EzyMemoryMonitor memoryMonitor = SystemMonitor.getInstance()
            .getMemoryMonitor();
        return MemoryPoint.builder()
            .maxMemory(memoryMonitor.getMaxMemory())
            .freeMemory(memoryMonitor.getFreeMemory())
            .totalMemory(memoryMonitor.getTotalMemory())
            .build();
    }
}
