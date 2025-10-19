package com.tvd12.ezyhttp.server.core.util;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.handler.IRequestController;

import static com.tvd12.ezyhttp.core.constant.Constants.DEFAULT_URI;

public final class ControllerAnnotations {

    private ControllerAnnotations() {}

    public static String getURI(Object controller) {
        Class<?> controllerClass = controller.getClass();
        Controller annotation = controllerClass
            .getAnnotation(Controller.class);
        if (annotation != null) {
            return getURI(controllerClass);
        }
        String uri = controller instanceof IRequestController
            ? ((IRequestController) controller).getDefaultUri()
            : DEFAULT_URI;
        return correctURI(uri);
    }

    public static String getURI(Class<?> controllerClass) {
        Controller annotation = controllerClass
            .getAnnotation(Controller.class);
        String uri = getURI(annotation);
        return correctURI(uri);
    }

    public static String getURI(Controller annotation) {
        if (annotation == null) {
            return DEFAULT_URI;
        }
        String uri = annotation.value();
        if (EzyStrings.isNoContent(uri)) {
            uri = annotation.uri();
        }
        if (EzyStrings.isNoContent(uri)) {
            uri = DEFAULT_URI;
        }
        return uri;
    }

    private static String correctURI(String uri) {
        String correctUri = uri;
        if (!uri.startsWith("/")) {
            correctUri = "/" + uri;
        }
        return correctUri;
    }
}
