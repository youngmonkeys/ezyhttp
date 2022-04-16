package com.tvd12.ezyhttp.core.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tvd12.ezyfox.io.EzyStringConveter;

public class DoubleArrayDeserializer extends StdDeserializer<Double[]> {
    private static final long serialVersionUID = -4497810070359275209L;
    
    public DoubleArrayDeserializer() {
        super(Double[].class);
    }

    @Override
    public Double[] deserialize(
            JsonParser p, 
            DeserializationContext ctxt
    ) throws IOException {
        if(p.currentTokenId() == JsonToken.VALUE_STRING.id()) {
            return EzyStringConveter.stringToWrapperDoubleArray(p.getValueAsString());
        }
        return ctxt.readValue(p, Double[].class);
    }

}
