package com.tvd12.ezyhttp.core.json;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyhttp.core.codec.SingletonStringDeserializer;

public class SetLongDeserializer extends StdDeserializer<Set<Long>> {
    private static final long serialVersionUID = -4497810070359275209L;
    
    public SetLongDeserializer() {
        super(Set.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Long> deserialize(
            JsonParser p, 
            DeserializationContext ctxt
    ) throws IOException, JsonProcessingException {
        if(p.currentTokenId() == JsonToken.VALUE_STRING.id()) {
            return SingletonStringDeserializer.getInstance().deserialize(
                    p.getValueAsString(),
                    Set.class,
                    Long.class
            );
        }
        return Sets.newHashSet(ctxt.readValue(p, Long[].class));
    }

}
