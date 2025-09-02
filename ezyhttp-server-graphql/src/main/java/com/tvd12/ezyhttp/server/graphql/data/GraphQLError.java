package com.tvd12.ezyhttp.server.graphql.data;

import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;

import java.util.*;

@Getter
public class GraphQLError {

    private final String message;
    private final List<Map<String, Object>> locations;
    private final List<String> path;
    private final Map<String, Object> extensions;

    protected GraphQLError(Builder builder) {
        this.message = builder.message;
        this.locations = builder.locations;
        this.path = builder.path;
        this.extensions = builder.extensions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<GraphQLError> {
        protected String message;
        protected List<Map<String, Object>> locations;
        protected List<String> path;
        protected Map<String, Object> extensions;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder location(Map<String, Object> location) {
            if (locations == null) {
                locations = new ArrayList<>();
            }
            locations.add(location);
            return this;
        }

        public Builder locations(
            List<Map<String, Object>> locations
        ) {
            if (this.locations == null) {
                this.locations = new ArrayList<>();
            }
            this.locations.addAll(locations);
            return this;
        }

        public Builder path(String... paths) {
            return path(Arrays.asList(paths));
        }

        public Builder path(List<String> path) {
            this.path = path;
            return this;
        }

        public Builder extension(String key, Object value) {
            if (extensions == null) {
                extensions = new HashMap<>();
            }
            extensions.put(key, value);
            return this;
        }

        public Builder extensions(Map<String, Object> extensions) {
            if (this.extensions == null) {
                this.extensions = new HashMap<>();
            }
            this.extensions.putAll(extensions);
            return this;
        }

        @Override
        public GraphQLError build() {
            return new GraphQLError(this);
        }
    }
}
