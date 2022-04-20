package com.tvd12.ezyhttp.server.core.util;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;

public final class TryCatchAnnotations {

    private TryCatchAnnotations() {
    }

    public static Class<?>[] getExceptionClasses(TryCatch tryCatch) {
        return tryCatch.value();
    }

    public static String getResponseType(TryCatch annotation) {
        return ContentTypes.APPLICATION_JSON;
    }
}
