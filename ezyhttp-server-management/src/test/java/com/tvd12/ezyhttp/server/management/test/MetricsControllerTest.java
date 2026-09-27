package com.tvd12.ezyhttp.server.management.test;

import java.io.File;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.monitor.data.EzyThreadsDetail;
import com.tvd12.ezyhttp.server.management.MetricsController;
import com.tvd12.ezyhttp.server.management.data.CpuPoint;
import com.tvd12.ezyhttp.server.management.data.DiskPoint;
import com.tvd12.ezyhttp.server.management.data.MemoryPoint;
import com.tvd12.ezyhttp.server.management.data.ThreadCountPoint;
import com.tvd12.ezyhttp.server.management.monitor.SystemMonitor;
import com.tvd12.test.assertion.Asserts;

public class MetricsControllerTest {

    @Test
    public void getStartTimeTest() {
        // given
        long before = System.currentTimeMillis();
        MetricsController sut = new MetricsController();
        long after = System.currentTimeMillis();

        // when
        long actual = sut.getStartTime();

        // then
        Asserts.assertTrue(actual >= before);
        Asserts.assertTrue(actual <= after);
    }

    @Test
    public void getLiveTimeTest() {
        // given
        MetricsController sut = new MetricsController();

        // when
        long actual = sut.getLiveTime();

        // then
        long expectedMax = (System.currentTimeMillis() - sut.getStartTime()) / 1000;
        Asserts.assertTrue(actual >= 0);
        Asserts.assertTrue(actual <= expectedMax);
    }

    @Test
    public void activeThreadsGetTest() {
        // given
        MetricsController sut = new MetricsController();

        // when
        EzyThreadsDetail actual = sut.activeThreadsGet();

        // then
        Asserts.assertNotNull(actual);
    }

    @Test
    public void threadsGetTest() {
        // given
        MetricsController sut = new MetricsController();

        // when
        ThreadCountPoint actual = sut.threadsGet();

        // then
        Asserts.assertTrue(actual.getThreadCount() > 0);
        Asserts.assertTrue(actual.getDaemonThreadCount() >= 0);
        Asserts.assertTrue(
            actual.getDaemonThreadCount() <= actual.getThreadCount()
        );
    }

    @Test
    public void cpuUsageGetTest() {
        // given
        MetricsController sut = new MetricsController();

        // when
        CpuPoint actual = sut.cpuUsageGet();

        // then
        Asserts.assertNotNull(actual);
        Asserts.assertTrue(actual.getProcessGcActivity() >= 0);
    }

    @Test
    public void memoryUsageGetTest() {
        // given
        MetricsController sut = new MetricsController();

        // when
        MemoryPoint actual = sut.memoryUsageGet();

        // then
        Asserts.assertTrue(actual.getMaxMemory() > 0);
        Asserts.assertTrue(actual.getTotalMemory() > 0);
        Asserts.assertTrue(actual.getFreeMemory() >= 0);
        Asserts.assertTrue(actual.getFreeMemory() <= actual.getTotalMemory());
        Asserts.assertEquals(actual.getAllocatedMemory(), actual.getTotalMemory());
        Asserts.assertEquals(
            actual.getUsedMemory(),
            actual.getTotalMemory() - actual.getFreeMemory()
        );
    }

    @Test
    public void freeDiskSpaceGetTest() {
        // given
        MetricsController sut = new MetricsController();
        File root = new File("/");

        // when
        DiskPoint actual = sut.freeDiskSpaceGet();

        // then
        Asserts.assertEquals(actual.getTotalSpace(), root.getTotalSpace());
        Asserts.assertTrue(actual.getFreeSpace() >= 0);
        Asserts.assertTrue(actual.getUsableSpace() >= 0);
        Asserts.assertTrue(actual.getFreeSpace() <= actual.getTotalSpace());
        Asserts.assertTrue(actual.getUsableSpace() <= actual.getTotalSpace());
    }

    @Test
    public void totalRequestGetTest() {
        // given
        MetricsController sut = new MetricsController();
        SystemMonitor monitor = SystemMonitor.getInstance();
        long before = sut.totalRequestGet();
        monitor.increaseRequestCount();

        // when
        long actual = sut.totalRequestGet();

        // then
        Asserts.assertTrue(actual >= before + 1);
        Asserts.assertTrue(actual <= monitor.getRequestCount());
    }

    @Test
    public void requestPerSecondGetTest() {
        // given
        MetricsController sut = new MetricsController();
        SystemMonitor.getInstance().increaseRequestCount();

        // when
        long actual = sut.requestPerSecondGet();

        // then
        Asserts.assertTrue(actual >= 1);
    }

    @Test
    public void responsePerSecondGetTest() {
        // given
        MetricsController sut = new MetricsController();
        SystemMonitor.getInstance().increaseResponseCount();

        // when
        long actual = sut.responsePerSecondGet();

        // then
        Asserts.assertTrue(actual >= 1);
    }
}
