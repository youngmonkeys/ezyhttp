package com.tvd12.ezyhttp.server.core.util;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.core.constant.Constants;
import com.tvd12.ezyhttp.server.core.annotation.Controller;

@SuppressWarnings("AbbreviationAsWordInName")
public final class ControllerAnnotations {

    private ControllerAnnotations() {
    }

    public static String getURI(Controller annotation) {
        if (annotation == null) {
            return Constants.DEFAULT_URI;
        }
        String uri = annotation.value();
        if (EzyStrings.isNoContent(uri)) {
            uri = annotation.uri();
        }
        if (EzyStrings.isNoContent(uri)) {
            uri = Constants.DEFAULT_URI;
        }
        return uri;
    }

    public static String getURI(Class<?> controllerClass) {
        Controller annotation = controllerClass.getAnnotation(Controller.class);
        String uri = ControllerAnnotations.getURI(annotation);
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }
        return uri;
    }

}
