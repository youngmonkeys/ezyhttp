package com.tvd12.ezyhttp.server.graphql.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyhttp.core.json.ObjectMapperBuilder;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;

public class GraphQLObjectMapperFactory {

    public ObjectMapper newObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapperBuilder().build();
        objectMapper.configure(ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(ALLOW_SINGLE_QUOTES, true);
        return objectMapper;
    }
}
