package com.tvd12.ezyhttp.core.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tvd12.ezyfox.io.EzyStringConveter;

public class LongArrayDeserializer extends StdDeserializer<Long[]> {
    private static final long serialVersionUID = -4497810070359275209L;

    public LongArrayDeserializer() {
        super(Long[].class);
    }

    @Override
    public Long[] deserialize(
        JsonParser p,
        DeserializationContext ctxt
    ) throws IOException {
        if (p.currentTokenId() == JsonToken.VALUE_STRING.id()) {
            return EzyStringConveter.stringToWrapperLongArray(p.getValueAsString());
        }
        return ctxt.readValue(p, Long[].class);
    }
}
