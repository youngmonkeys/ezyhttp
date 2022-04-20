package com.tvd12.ezyhttp.server.core.asm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.handler.RequestHandler;
import com.tvd12.ezyhttp.server.core.handler.RequestURIDecorator;
import com.tvd12.ezyhttp.server.core.reflect.ControllerProxy;
import com.tvd12.ezyhttp.server.core.reflect.RequestHandlerMethod;
import com.tvd12.ezyhttp.server.core.request.RequestURI;
import com.tvd12.ezyhttp.server.core.request.RequestURIMeta;

import lombok.Setter;

public class RequestHandlersImplementer extends EzyLoggable {

    @Setter
    private RequestURIDecorator requestURIDecorator;

    public Map<RequestURI, List<RequestHandler>> implement(
        Collection<Object> controllers
    ) {
        Map<RequestURI, List<RequestHandler>> handlers = new HashMap<>();
        for (Object controller : controllers) {
            Map<RequestURI, List<RequestHandler>> map = implement(controller);
            for (RequestURI uri : map.keySet()) {
                handlers.computeIfAbsent(uri, k -> new ArrayList<>())
                    .addAll(map.get(uri));
            }
        }
        return handlers;
    }

    public Map<RequestURI, List<RequestHandler>> implement(Object controller) {
        Map<RequestURI, List<RequestHandler>> handlers = new HashMap<>();
        ControllerProxy proxy = new ControllerProxy(controller);
        String feature = proxy.getFeature();
        for (RequestHandlerMethod method : proxy.getRequestHandlerMethods()) {
            RequestHandlerImplementer implementer = newImplementer(proxy, method);
            RequestHandler handler = implementer.implement();
            HttpMethod httpMethod = handler.getMethod();
            String requestURI = handler.getRequestURI();
            String methodFeature = method.getFeature();
            RequestURIMeta uriMeta = RequestURIMeta.builder()
                .api(method.isApi() || proxy.isApi())
                .authenticated(method.isAuthenticated() || proxy.isAuthenticated())
                .management(method.isManagement() || proxy.isManagement())
                .payment(method.isPayment() || proxy.isPayment())
                .feature(methodFeature != null ? methodFeature : feature)
                .build();
            RequestURI uri = new RequestURI(httpMethod, requestURI, uriMeta);
            handlers.computeIfAbsent(uri, k -> new ArrayList<>())
                .add(handler);
        }
        return handlers;
    }

    protected RequestHandlerImplementer newImplementer(
        ControllerProxy controller,
        RequestHandlerMethod method
    ) {
        RequestHandlerImplementer answer = new RequestHandlerImplementer(controller, method);
        answer.setRequestURIDecorator(requestURIDecorator);
        return answer;
    }
}
