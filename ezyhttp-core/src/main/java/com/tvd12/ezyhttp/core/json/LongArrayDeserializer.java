package com.tvd12.ezyhttp.core.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tvd12.ezyfox.io.EzyStringConveter;

public class LongArrayDeserializer extends StdDeserializer<long[]> {
    private static final long serialVersionUID = -4497810070359275209L;
    
    public LongArrayDeserializer() {
        super(long[].class);
    }

    @Override
    public long[] deserialize(
            JsonParser p, 
            DeserializationContext ctxt
    ) throws IOException, JsonProcessingException {
        if(p.currentTokenId() == JsonToken.VALUE_STRING.id()) {
            return EzyStringConveter.stringToPrimitiveLongArray(p.getValueAsString());
        }
        return ctxt.readValue(p, long[].class);
    }

}
