package com.tvd12.ezyhttp.server.graphql.data;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;

@Getter
public class GraphQLArgument {

    protected final String name;
    protected final Object value;

    protected GraphQLArgument(Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
    }

    @Override
    public String toString() {
        return name + ":" + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof GraphQLArgument) {
            return name.equals(((GraphQLArgument) obj).name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<GraphQLArgument> {
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
        public GraphQLArgument build() {
            return new GraphQLArgument(this);
        }
    }
}
