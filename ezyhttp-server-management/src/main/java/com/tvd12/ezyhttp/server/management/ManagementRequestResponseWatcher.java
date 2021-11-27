package com.tvd12.ezyhttp.server.management;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.tvd12.ezyfox.annotation.EzyProperty;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.RequestResponseWatcher;
import com.tvd12.ezyhttp.server.management.monitor.SystemMonitor;

import lombok.Setter;

@Setter
@EzySingleton
public class ManagementRequestResponseWatcher implements RequestResponseWatcher {

    @EzyProperty("management.port")
    protected int managementPort;
    
    @Override
    public void watchRequest(
            HttpMethod method, 
            ServletRequest request
    ) {
        if (request.getServerPort() != managementPort) {
            SystemMonitor.getInstance().increaseRequestCount();
        }
    }
    
    @Override
    public void watchResponse(
            HttpMethod method, 
            ServletRequest request, 
            ServletResponse response
    ) {
        if (request.getServerPort() != managementPort) {
            SystemMonitor.getInstance().increaseResponseCount();
        }
    }
}
