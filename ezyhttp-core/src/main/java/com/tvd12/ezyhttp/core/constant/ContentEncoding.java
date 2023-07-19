package com.tvd12.ezyhttp.core.constant;

import com.tvd12.ezyfox.util.EzyEnums;
import lombok.Getter;

import java.util.Map;

@Getter
public enum ContentEncoding {
    
    GZIP(ContentType.GZIP, "gzip");
    
    private final ContentType contentType;
    private final String value;
    
    private static final Map<ContentType, ContentEncoding> VALUE_BY_CONTENT_TYPE =
        EzyEnums.enumMap(ContentEncoding.class, it -> it.contentType);

    ContentEncoding(ContentType contentType, String value) {
        this.contentType = contentType;
        this.value = value;
    }
    
    public static ContentEncoding ofMimeType(String mimeType) {
        ContentType contentType = ContentType.ofMimeType(mimeType);
        return VALUE_BY_CONTENT_TYPE.get(contentType);
    }
}
