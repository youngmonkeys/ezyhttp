package com.tvd12.ezyhttp.core.codec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.tvd12.ezyfox.stream.EzyInputStreams;
import com.tvd12.ezyhttp.core.data.BodyData;

import static com.tvd12.ezyfox.stream.EzyInputStreams.DEFAULT_BUFFER_SIZE;

public interface BodyDeserializer {

    default <T> T deserialize(
        String data,
        Class<T> bodyType
    ) throws IOException {
        return null;
    }

    default <T> T deserialize(
        BodyData data,
        Class<T> bodyType
    ) throws IOException {
        return null;
    }

    default <T> T deserialize(
        InputStream inputStream,
        Class<T> bodyType
    ) throws IOException {
        return null;
    }

    default String deserializeToString(
        InputStream inputStream,
        int contentLength
    ) throws IOException {
        byte[] bytes = EzyInputStreams.toByteArray(
            inputStream,
            contentLength > 0 ? contentLength : DEFAULT_BUFFER_SIZE
        );
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
