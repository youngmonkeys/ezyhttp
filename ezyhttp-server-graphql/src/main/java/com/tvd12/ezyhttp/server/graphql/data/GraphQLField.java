package com.tvd12.ezyhttp.server.graphql.data;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.io.EzySingletonOutputTransformer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyMaps.newHashMap;

@Getter
public class GraphQLField {

    protected final String name;
    protected final Map<String, Object> arguments;
    protected final List<GraphQLField> fields;
    protected final Map<String, GraphQLField> fieldByName;

    protected GraphQLField(Builder builder) {
        this.name = builder.name;
        this.arguments = builder.arguments;
        this.fields = builder.fields != null
            ? builder.fields
            : Collections.emptyList();
        this.fieldByName = newHashMap(
            fields,
            GraphQLField::getName
        );
    }

    public GraphQLField getField(String fieldName) {
        return fieldByName.get(fieldName);
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgumentValue(
        String argumentName
    ) {
        return arguments != null
            ? (T) arguments.get(argumentName)
            : null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgumentValue(
        String argumentName,
        Class<T> type
    ) {
        Object value = getArgumentValue(argumentName);
        if (value == null) {
            return null;
        }
        return (T) EzySingletonOutputTransformer
            .getInstance()
            .transform(value, type);
    }

    public <T> T getFieldArgumentValue(
        String argumentName,
        String... fieldNames
    ) {
        GraphQLField field = this;
        for (String fieldName : fieldNames) {
            field = fieldByName.get(fieldName);
            if (field == null) {
                break;
            }
        }
        return field != null
            ? field.getArgumentValue(argumentName)
            : null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getFieldArgumentValue(
        String argumentName,
        Class<T> type,
        String... fieldNames
    ) {
        Object value = getFieldArgumentValue(
            argumentName,
            fieldNames
        );
        if (value == null) {
            return null;
        }
        return (T) EzySingletonOutputTransformer
            .getInstance()
            .transform(value, type);
    }

    @Override
    public String toString() {
        return toString(name, arguments, fields);
    }

    private static String toString(
        String name,
        Map<String, Object> arguments,
        List<GraphQLField> fields
    ) {
        StringBuilder builder = new StringBuilder();
        if (name != null) {
            builder.append(name);
        }
        if (arguments != null && !arguments.isEmpty()) {
            builder.append("(").append(arguments).append(")");
        }
        if (fields != null) {
            builder.append(", ").append(fields);
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof GraphQLField) {
            return name.equals(((GraphQLField) obj).name);
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

    public static class Builder implements EzyBuilder<GraphQLField> {
        private String name;
        private Map<String, Object> arguments;
        private List<GraphQLField> fields;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder arguments(Map<String, Object> arguments) {
            this.arguments = arguments;
            return this;
        }

        public Builder addField(GraphQLField field) {
            if (fields == null) {
                fields = new ArrayList<>();
            }
            this.fields.add(field);
            return this;
        }

        @Override
        public GraphQLField build() {
            return new GraphQLField(this);
        }

        @Override
        public String toString() {
            return GraphQLField.toString(name, arguments, fields);
        }
    }
}
