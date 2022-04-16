package com.tvd12.ezyhttp.core.json;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tvd12.ezyfox.collect.Lists;
import com.tvd12.ezyhttp.core.codec.SingletonStringDeserializer;

public class ListIntegerDeserializer extends StdDeserializer<List<Integer>> {
    private static final long serialVersionUID = -4497810070359275209L;
    
    public ListIntegerDeserializer() {
        super(List.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Integer> deserialize(
            JsonParser p, 
            DeserializationContext ctxt
    ) throws IOException {
        if(p.currentTokenId() == JsonToken.VALUE_STRING.id()) {
            return SingletonStringDeserializer.getInstance().deserialize(
                    p.getValueAsString(),
                    List.class,
                    Integer.class
            );
        }
        return Lists.newArrayList(ctxt.readValue(p, Integer[].class));
    }

}
