package com.tvd12.ezyhttp.server.graphql.scheme;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class GraphQLDataSchema {
    protected final String name;
    protected final String type;
    protected final String format;
    protected final String description;
    protected final String example;
    protected final List<GraphQLDataSchema> items;
    protected final List<GraphQLDataSchema> properties;

    protected GraphQLDataSchema(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.format = builder.format;
        this.description = builder.description;
        this.example = builder.example;
        this.items = builder.items;
        this.properties = builder.properties;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<GraphQLDataSchema> {
        protected String name;
        protected String type;
        protected String format;
        protected String description;
        protected String example;
        protected List<GraphQLDataSchema> items;
        protected List<GraphQLDataSchema> properties;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder example(String example) {
            this.example = example;
            return this;
        }

        public Builder items(List<GraphQLDataSchema> items) {
            this.items = items;
            return this;
        }

        public Builder properties(List<GraphQLDataSchema> properties) {
            this.properties = properties;
            return this;
        }

        @Override
        public GraphQLDataSchema build() {
            return new GraphQLDataSchema(this);
        }
    }
}
