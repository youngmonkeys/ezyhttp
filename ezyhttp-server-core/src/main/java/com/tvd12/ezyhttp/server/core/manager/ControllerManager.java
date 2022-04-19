package com.tvd12.ezyhttp.server.core.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tvd12.ezyfox.util.EzyDestroyable;

import lombok.Getter;

public class ControllerManager implements EzyDestroyable {

    @Getter
    protected final List<Object> controllers;
    
    public ControllerManager() {
        this.controllers = new ArrayList<>();
    }
    
    public void addController(Object controller) {
        this.controllers.add(controller);
    }
    
    public void addControllers(Collection<?> controllers) {
        for (Object controller : controllers)
            addController(controller);
    }
    
    @Override
    public void destroy() {
        this.controllers.clear();
    }
}
