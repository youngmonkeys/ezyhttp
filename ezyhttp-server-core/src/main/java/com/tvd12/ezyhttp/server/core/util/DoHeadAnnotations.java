package com.tvd12.ezyhttp.server.core.util;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.core.constant.Constants;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.server.core.annotation.DoHead;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DoHeadAnnotations {

    private DoHeadAnnotations() {}

    public static String getURI(DoHead annotation) {
        String uri = annotation.value();
        if (EzyStrings.isNoContent(uri)) {
            uri = annotation.uri();
        }
        if (EzyStrings.isNoContent(uri)) {
            uri = Constants.EMPTY_STRING;
        }
        return uri;
    }

    public static String getResponseType(DoHead annotation) {
        String responseType = annotation.responseType();
        if (EzyStrings.isNoContent(responseType)) {
            responseType = ContentTypes.APPLICATION_JSON;
        }
        return responseType;
    }

    public static Set<String> getAccept(DoHead annotation) {
        return Stream
            .of(annotation.accept())
            .filter(EzyStrings::isNotBlank)
            .map(it -> it.trim().toLowerCase(Locale.ROOT))
            .collect(Collectors.toSet());
    }
}
