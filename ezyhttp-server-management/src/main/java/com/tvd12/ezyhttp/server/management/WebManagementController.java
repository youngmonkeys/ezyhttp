package com.tvd12.ezyhttp.server.management;

import java.util.List;
import java.util.stream.Collectors;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.handler.ManagementController;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.management.data.ApiInformation;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class WebManagementController implements ManagementController {
    
    private final RequestHandlerManager requestHandlerManager;

    @DoGet("/management/apis")
    public List<ApiInformation> apiListGet() {
        return requestHandlerManager.getHandlerListByURI()
            .entrySet()
            .stream()
            .map(it ->
                new ApiInformation(it.getKey(), it.getValue())
            )
            .collect(Collectors.toList());
    }
}
