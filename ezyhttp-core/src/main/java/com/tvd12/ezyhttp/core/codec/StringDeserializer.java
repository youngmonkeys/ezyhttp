package com.tvd12.ezyhttp.core.codec;

import java.io.IOException;

public interface StringDeserializer {

    <T> T deserialize(
        String value,
        Class<T> outType,
        Class<?> genericType
    ) throws IOException;
}
