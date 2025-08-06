package com.tvd12.ezyhttp.server.graphql.scheme;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
public class GraphQLSchema {

    private final List<GraphQLQueryDefinition> queryDefinitions;

    public GraphQLSchema(Builder builder) {
        this.queryDefinitions = builder.queryDefinitions != null
            ? builder.queryDefinitions
            : Collections.emptyList();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<GraphQLSchema> {
        private List<GraphQLQueryDefinition> queryDefinitions;

        public Builder addQueryDefinition(GraphQLQueryDefinition queryDefinition) {
            if (queryDefinitions == null) {
                queryDefinitions = new LinkedList<>();
            }
            this.queryDefinitions.add(queryDefinition);
            return this;
        }

        @Override
        public GraphQLSchema build() {
            return new GraphQLSchema(this);
        }
    }
}
