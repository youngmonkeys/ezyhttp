package com.tvd12.ezyhttp.server.graphql.scheme;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class GraphQLDataFetcherSchema {
    protected final List<GraphQLDataSchema> queryScheme;
    protected final GraphQLDataSchema getResponseScheme;

    protected GraphQLDataFetcherSchema(Builder builder) {
        this.queryScheme = builder.queryScheme;
        this.getResponseScheme = builder.getResponseScheme;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<GraphQLDataFetcherSchema> {
        protected List<GraphQLDataSchema> queryScheme;
        protected GraphQLDataSchema getResponseScheme;

        public Builder queryScheme(List<GraphQLDataSchema> queryScheme) {
            this.queryScheme = queryScheme;
            return this;
        }

        public Builder getResponseScheme(GraphQLDataSchema getResponseScheme) {
            this.getResponseScheme = getResponseScheme;
            return this;
        }

        @Override
        public GraphQLDataFetcherSchema build() {
            return new GraphQLDataFetcherSchema(this);
        }
    }
}
