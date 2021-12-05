package com.tvd12.ezyhttp.server.core.handler;

import com.tvd12.ezyfox.reflect.EzyClass;

public interface RequestURIDecorator {

    String decorate(EzyClass controllerClass, String originURI);
}
