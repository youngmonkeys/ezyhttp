package com.tvd12.ezyhttp.server.management.test.data;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.management.data.CpuPoint;
import com.tvd12.ezyhttp.server.management.data.DiskPoint;
import com.tvd12.ezyhttp.server.management.data.MemoryPoint;
import com.tvd12.ezyhttp.server.management.data.ThreadCountPoint;
import com.tvd12.test.assertion.Asserts;

public class DataPointsTest {

    @Test
    public void cpuPointTest() {
        // given
        // when
        CpuPoint sut = CpuPoint.builder()
            .systemCpuLoad(0.5D)
            .processCpuLoad(0.25D)
            .processGcActivity(0.1D)
            .build();

        // then
        Asserts.assertEquals(sut.getSystemCpuLoad(), 0.5D);
        Asserts.assertEquals(sut.getProcessCpuLoad(), 0.25D);
        Asserts.assertEquals(sut.getProcessGcActivity(), 0.1D);
    }

    @Test
    public void diskPointTest() {
        // given
        // when
        DiskPoint sut = DiskPoint.builder()
            .freeSpace(100L)
            .totalSpace(300L)
            .usableSpace(80L)
            .build();

        // then
        Asserts.assertEquals(sut.getFreeSpace(), 100L);
        Asserts.assertEquals(sut.getTotalSpace(), 300L);
        Asserts.assertEquals(sut.getUsableSpace(), 80L);
    }

    @Test
    public void memoryPointTest() {
        // given
        // when
        MemoryPoint sut = MemoryPoint.builder()
            .maxMemory(1000L)
            .freeMemory(200L)
            .totalMemory(500L)
            .build();

        // then
        Asserts.assertEquals(sut.getMaxMemory(), 1000L);
        Asserts.assertEquals(sut.getFreeMemory(), 200L);
        Asserts.assertEquals(sut.getTotalMemory(), 500L);
        Asserts.assertEquals(sut.getAllocatedMemory(), 500L);
        Asserts.assertEquals(sut.getUsedMemory(), 300L);
    }

    @Test
    public void threadCountPointTest() {
        // given
        // when
        ThreadCountPoint sut = ThreadCountPoint.builder()
            .threadCount(10)
            .daemonThreadCount(4)
            .build();

        // then
        Asserts.assertEquals(sut.getThreadCount(), 10);
        Asserts.assertEquals(sut.getDaemonThreadCount(), 4);
    }
}
