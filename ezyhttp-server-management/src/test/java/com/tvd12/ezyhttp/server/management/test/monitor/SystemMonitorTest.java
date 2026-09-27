package com.tvd12.ezyhttp.server.management.test.monitor;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.management.monitor.SystemMonitor;
import com.tvd12.test.assertion.Asserts;

public class SystemMonitorTest {

    @Test
    public void getInstanceTest() {
        // given
        // when
        SystemMonitor actual = SystemMonitor.getInstance();

        // then
        Asserts.assertNotNull(actual);
        Asserts.assertTrue(actual == SystemMonitor.INSTANCE);
        Asserts.assertTrue(actual == SystemMonitor.getInstance());
    }

    @Test
    public void monitorsTest() {
        // given
        SystemMonitor sut = SystemMonitor.getInstance();

        // when
        // then
        Asserts.assertNotNull(sut.getCpuMonitor());
        Asserts.assertNotNull(sut.getGcMonitor());
        Asserts.assertNotNull(sut.getMemoryMonitor());
        Asserts.assertNotNull(sut.getThreadsMonitor());
    }

    @Test
    public void increaseRequestCountTest() {
        // given
        SystemMonitor sut = SystemMonitor.getInstance();
        long before = sut.getRequestCount();

        // when
        sut.increaseRequestCount();

        // then
        Asserts.assertTrue(sut.getRequestCount() >= before + 1);
        Asserts.assertTrue(sut.getRequestPerSecond() >= 1);
    }

    @Test
    public void increaseResponseCountTest() {
        // given
        SystemMonitor sut = SystemMonitor.getInstance();

        // when
        sut.increaseResponseCount();

        // then
        Asserts.assertTrue(sut.getResponsePerSecond() >= 1);
    }
}
