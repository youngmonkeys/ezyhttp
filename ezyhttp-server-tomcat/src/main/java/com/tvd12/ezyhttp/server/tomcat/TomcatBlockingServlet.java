package com.tvd12.ezyhttp.server.tomcat;

import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.ezyhttp.server.core.servlet.BlockingServlet;

public class TomcatBlockingServlet extends BlockingServlet {
    private static final long serialVersionUID = 1321397014207226911L;

    public TomcatBlockingServlet(ComponentManager componentManager) {
        super(componentManager);
    }
}
