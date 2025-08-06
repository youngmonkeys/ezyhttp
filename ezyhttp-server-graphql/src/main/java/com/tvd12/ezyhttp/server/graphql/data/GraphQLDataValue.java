package com.tvd12.ezyhttp.server.graphql.data;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;

@Getter
public class GraphQLDataValue {

    protected final String name;
    protected final Object value;

    protected GraphQLDataValue(Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
    }

    @Override
    public String toString() {
        return name + ":" + value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<GraphQLDataValue> {
        private String name;
        private Object value;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        @Override
        public GraphQLDataValue build() {
            return new GraphQLDataValue(this);
        }
    }
}
