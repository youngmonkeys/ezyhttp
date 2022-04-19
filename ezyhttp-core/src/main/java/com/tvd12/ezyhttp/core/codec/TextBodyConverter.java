package com.tvd12.ezyhttp.core.codec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.jackson.JacksonObjectMapperBuilder;
import com.tvd12.ezyfox.stream.EzyInputStreams;
import com.tvd12.ezyhttp.core.data.BodyData;

public class TextBodyConverter implements BodyConverter {

    protected final ObjectMapper objectMapper;
    
    public TextBodyConverter() {
        this(JacksonObjectMapperBuilder.newInstance().build());
    }
    
    public TextBodyConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public byte[] serialize(Object body) throws IOException {
        byte[] bytes;
        try {
            bytes = body.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IOException("serialize body: " + body + " error", e);
        }
        return bytes;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(String data, Class<T> bodyType) throws IOException {
        if (bodyType == String.class) {
            return (T) data;
        }
        return objectMapper.readValue(data, bodyType);
    }
    
    @Override
    public <T> T deserialize(BodyData data, Class<T> bodyType) throws IOException {
        return deserialize(data.getInputStream(), bodyType);
    }
    
    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> bodyType) throws IOException {
        String data = EzyInputStreams.toStringUtf8(inputStream);
        return deserialize(data, bodyType);
    }

}
