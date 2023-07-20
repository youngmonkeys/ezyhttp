package com.tvd12.ezyhttp.core.constant;

import com.tvd12.ezyfox.util.EzyEnums;
import lombok.Getter;

import java.util.Map;

@Getter
public enum ContentEncoding {
    
    GZIP(ContentType.GZIP.getMimeType(), "gzip");
    
    private final String mimeType;
    private final String value;
    
    private static final Map<String, ContentEncoding> VALUE_BY_MIME_TYPE =
        EzyEnums.enumMap(ContentEncoding.class, it -> it.mimeType);

    ContentEncoding(String mimeType, String value) {
        this.mimeType = mimeType;
        this.value = value;
    }
    
    public static ContentEncoding ofMimeType(String mimeType) {
        return VALUE_BY_MIME_TYPE.get(mimeType);
    }
}
