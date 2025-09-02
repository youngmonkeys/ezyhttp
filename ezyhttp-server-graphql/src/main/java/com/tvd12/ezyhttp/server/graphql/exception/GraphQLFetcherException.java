package com.tvd12.ezyhttp.server.graphql.exception;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLError;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GraphQLFetcherException extends RuntimeException {

    private final Map<String, Object> data;
    private final List<GraphQLError> errors;

    protected GraphQLFetcherException(Builder builder) {
        super("data: " + builder.data + ", errors: " + builder.errors);
        this.data = builder.data;
        this.errors = builder.errors;
    }

    public Map<String, Object> toDataMap() {
        Map<String, Object> answer = new HashMap<>();
        if (data != null) {
            answer.put("data", data);
        }
        answer.put("errors", errors);
        return answer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<GraphQLFetcherException> {
        protected Map<String, Object> data;
        protected List<GraphQLError> errors;

        public Builder data(Map<String, Object> data) {
            if (this.data == null) {
                this.data = new HashMap<>();
            }
            this.data.putAll(data);
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder errorDataFieldAndPaths(
            String errorDataField,
            String... paths
        ) {
            if (data == null) {
                data = new HashMap<>();
            }
            Map<String, Object> lastObject = data;
            for (String path : paths) {
                Object object = lastObject.get(path);
                if (object instanceof Map) {
                    lastObject = (Map<String, Object>) object;
                } else {
                    Map<String, Object> map = new HashMap<>();
                    lastObject.put(path, map);
                    lastObject = map;
                }
            }
            lastObject.put(errorDataField, null);
            return this;
        }

        public Builder error(GraphQLError error) {
            if (errors == null) {
                errors = new ArrayList<>();
            }
            errors.add(error);
            return this;
        }

        public Builder errors(List<GraphQLError> errors) {
            if (this.errors == null) {
                this.errors = new ArrayList<>();
            }
            this.errors.addAll(errors);
            return this;
        }

        @Override
        public GraphQLFetcherException build() {
            return new GraphQLFetcherException(this);
        }
    }
}
