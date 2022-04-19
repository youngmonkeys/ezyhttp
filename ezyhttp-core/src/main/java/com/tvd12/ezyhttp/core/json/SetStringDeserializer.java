package com.tvd12.ezyhttp.core.json;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyhttp.core.codec.SingletonStringDeserializer;

public class SetStringDeserializer extends StdDeserializer<Set<String>> {
    private static final long serialVersionUID = -4497810070359275209L;

    public SetStringDeserializer() {
        super(Set.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> deserialize(
        JsonParser p,
        DeserializationContext ctxt
    ) throws IOException {
        if (p.currentTokenId() == JsonToken.VALUE_STRING.id()) {
            return SingletonStringDeserializer.getInstance().deserialize(
                p.getValueAsString(),
                Set.class,
                String.class
            );
        }
        return Sets.newHashSet(ctxt.readValue(p, String[].class));
    }
}
