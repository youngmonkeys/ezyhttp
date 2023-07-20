package com.tvd12.ezyhttp.core.resources;

import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.core.constant.ContentTypes;

import java.util.Map;
import java.util.Set;

import static com.tvd12.ezyhttp.core.constant.Constants.EMPTY_STRING;

public class ActualContentTypeDetector {
    
    private final Set<String> needToDetectActualContentTypes;
    private final Map<String, String> actualContentTypeByExtension;
    private static final ActualContentTypeDetector INSTANCE =
        new ActualContentTypeDetector();

    private ActualContentTypeDetector() {
        this.needToDetectActualContentTypes = Sets.newHashSet(
            ContentTypes.GZIP
        );
        actualContentTypeByExtension = EzyMapBuilder.mapBuilder()
            .put("wasm.gz", ContentTypes.APPLICATION_WASM)
            .toMap();
    }
    
    public static ActualContentTypeDetector getInstance() {
        return INSTANCE;
    }

    public String detect(
        String resourcePath,
        String originalContentType
    ) {
        if (!needToDetectActualContentTypes.contains(originalContentType)) {
            return originalContentType;
        }
        String twoPartsExtension = extractTwoPartsExtension(resourcePath);
        return actualContentTypeByExtension.getOrDefault(
            twoPartsExtension,
            originalContentType
        );
    }

    private String extractTwoPartsExtension(String resourcePath) {
        int dotCount = 0;
        for (int i = resourcePath.length() - 1; i >= 0; --i) {
            char ch = resourcePath.charAt(i);
            if (ch == '.') {
                ++dotCount;
            }
            if (dotCount == 2) {
                return resourcePath.substring(i + 1);
            }
            if (ch == '/' || ch == '\\') {
                break;
            }
        }
        return EMPTY_STRING;
    }
}
