package com.tvd12.ezyhttp.server.management;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.RequestResponseWatcher;
import com.tvd12.ezyhttp.server.management.monitor.SystemMonitor;

@EzySingleton
public class ManagementRequestResponseWatcher implements RequestResponseWatcher {

    @Override
    public void watchRequest(
            HttpMethod method, 
            ServletRequest request
    ) {
        SystemMonitor.getInstance().increaseRequestCount();
    }
    
    @Override
    public void watchResponse(
            HttpMethod method, 
            ServletRequest request, 
            ServletResponse response
    ) {
        SystemMonitor.getInstance().increaseResponseCount();
    }
}
