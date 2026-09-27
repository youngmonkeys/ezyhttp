package com.tvd12.ezyhttp.server.management.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.management.ManagementRequestResponseWatcher;
import com.tvd12.ezyhttp.server.management.monitor.SystemMonitor;
import com.tvd12.test.assertion.Asserts;

public class ManagementRequestResponseWatcherTest {

    @Test
    public void watchRequestNotManagementPortTest() {
        // given
        ManagementRequestResponseWatcher sut = new ManagementRequestResponseWatcher();
        sut.setManagementPort(18081);

        ServletRequest request = mock(ServletRequest.class);
        when(request.getServerPort()).thenReturn(8080);

        SystemMonitor monitor = SystemMonitor.getInstance();
        long before = monitor.getRequestCount();

        // when
        sut.watchRequest(HttpMethod.GET, request);

        // then
        Asserts.assertTrue(monitor.getRequestCount() >= before + 1);
        Asserts.assertTrue(monitor.getRequestPerSecond() >= 1);

        verify(request, times(1)).getServerPort();
        verifyNoMoreInteractions(request);
    }

    @Test
    public void watchRequestManagementPortTest() {
        // given
        ManagementRequestResponseWatcher sut = new ManagementRequestResponseWatcher();
        sut.setManagementPort(18081);

        ServletRequest request = mock(ServletRequest.class);
        when(request.getServerPort()).thenReturn(18081);

        SystemMonitor monitor = SystemMonitor.getInstance();
        long before = monitor.getRequestCount();

        // when
        sut.watchRequest(HttpMethod.GET, request);

        // then
        Asserts.assertEquals(monitor.getRequestCount(), before);

        verify(request, times(1)).getServerPort();
        verifyNoMoreInteractions(request);
    }

    @Test
    public void watchResponseNotManagementPortTest() {
        // given
        ManagementRequestResponseWatcher sut = new ManagementRequestResponseWatcher();
        sut.setManagementPort(18081);

        ServletRequest request = mock(ServletRequest.class);
        when(request.getServerPort()).thenReturn(8080);
        ServletResponse response = mock(ServletResponse.class);

        SystemMonitor monitor = SystemMonitor.getInstance();

        // when
        sut.watchResponse(HttpMethod.GET, request, response);

        // then
        Asserts.assertTrue(monitor.getResponsePerSecond() >= 1);

        verify(request, times(1)).getServerPort();
        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(response);
    }

    @Test
    public void watchResponseManagementPortTest() {
        // given
        ManagementRequestResponseWatcher sut = new ManagementRequestResponseWatcher();
        sut.setManagementPort(18081);

        ServletRequest request = mock(ServletRequest.class);
        when(request.getServerPort()).thenReturn(18081);
        ServletResponse response = mock(ServletResponse.class);

        SystemMonitor monitor = SystemMonitor.getInstance();
        long before = monitor.getRequestCount();

        // when
        sut.watchResponse(HttpMethod.GET, request, response);

        // then
        Asserts.assertEquals(monitor.getRequestCount(), before);

        verify(request, times(1)).getServerPort();
        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(response);
    }
}
