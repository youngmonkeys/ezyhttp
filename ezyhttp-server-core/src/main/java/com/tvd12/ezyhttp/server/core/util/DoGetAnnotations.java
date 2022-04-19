package com.tvd12.ezyhttp.server.core.util;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.core.constant.Constants;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;

public final class DoGetAnnotations {

    private DoGetAnnotations() {}
    
    public static String getURI(DoGet annotation) {
        String uri = annotation.value();
        if (EzyStrings.isNoContent(uri))
            uri = annotation.uri();
        if (EzyStrings.isNoContent(uri))
            uri = Constants.EMPTY_STRING;
        return uri;
    }
    
    public static String getResponseType(DoGet annotation) {
        String responseType = annotation.responseType();
        if (EzyStrings.isNoContent(responseType))
            responseType = ContentTypes.APPLICATION_JSON;
        return responseType;
    }
}
