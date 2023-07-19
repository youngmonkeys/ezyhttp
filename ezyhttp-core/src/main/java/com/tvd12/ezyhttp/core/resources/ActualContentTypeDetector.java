package com.tvd12.ezyhttp.core.resources;

import com.tvd12.ezyhttp.core.constant.ContentTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActualContentTypeDetector {
    
    private static final Set<String> NEED_TO_DETECT_ACTUAL_CONTENT_TYPES = new HashSet<>();
    private static final Map<String, String> ACTUAL_CONTENT_TYPE_BY_EXTENSION = new HashMap<>();
    private static final ActualContentTypeDetector INSTANCE = new ActualContentTypeDetector();

    private ActualContentTypeDetector() {}
    
    public static ActualContentTypeDetector getInstance() {
        return INSTANCE;
    }

    static {
        NEED_TO_DETECT_ACTUAL_CONTENT_TYPES.add(ContentTypes.GZIP);
        ACTUAL_CONTENT_TYPE_BY_EXTENSION.put("wasm.gz", ContentTypes.APPLICATION_WASM);
    }
    
    public String detect(
        String resourcePath,
        String originalContentType
    ) {
        if (!NEED_TO_DETECT_ACTUAL_CONTENT_TYPES.contains(originalContentType)) {
            return originalContentType;
        }
        int lastPeriod = resourcePath.lastIndexOf('.');
        int secondLastPeriod = resourcePath.lastIndexOf('.', lastPeriod - 1);
        if (secondLastPeriod == -1) {
            return originalContentType;
        } else {
            String actualExtension = resourcePath.substring(secondLastPeriod + 1);
            if (!ACTUAL_CONTENT_TYPE_BY_EXTENSION.containsKey(actualExtension)) {
                return originalContentType;
            }
            return ACTUAL_CONTENT_TYPE_BY_EXTENSION.get(actualExtension);
        }
    }
}
