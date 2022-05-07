package com.tvd12.ezyhttp.core.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tvd12.ezyfox.io.EzyStringConverter;

public class IntegerArrayDeserializer extends StdDeserializer<Integer[]> {
    private static final long serialVersionUID = -4497810070359275209L;

    public IntegerArrayDeserializer() {
        super(Integer[].class);
    }

    @Override
    public Integer[] deserialize(
        JsonParser p,
        DeserializationContext ctxt
    ) throws IOException {
        if (p.currentTokenId() == JsonToken.VALUE_STRING.id()) {
            return EzyStringConverter.stringToWrapperIntArray(p.getValueAsString());
        }
        return ctxt.readValue(p, Integer[].class);
    }
}
