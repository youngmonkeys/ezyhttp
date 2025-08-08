package com.tvd12.ezyhttp.server.graphql.data;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.io.EzySingletonOutputTransformer;
import com.tvd12.ezyhttp.core.codec.SingletonStringDeserializer;
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

    /**
     * Get argument value by name.
     *
     * @param argumentName the argument name.
     * @return the argument value.
     * @param <T> the value type.
     */
    @SuppressWarnings("unchecked")
    public <T> T getArgumentValue(
        String argumentName
    ) {
        return arguments != null
            ? (T) arguments.get(argumentName)
            : null;
    }

    /**
     * Get argument value by name and transforms it into the desired type.
     *
     * @param argumentName the argument name.
     * @param type the argument type class.
     * @return the argument value.
     * @param <T> the argument type.
     */
    @SuppressWarnings("unchecked")
    public <T> T getArgumentValue(
        String argumentName,
        Class<T> type
    ) {
        Object value = getArgumentValue(argumentName);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return SingletonStringDeserializer
                .getInstance()
                .deserializeOrNull((String) value, type);
        }
        return (T) EzySingletonOutputTransformer
            .getInstance()
            .transform(value, type);
    }

    /**
     * Retrieves the value of a specified argument from a nested GraphQL field path.
     *
     * @param argumentName the name of the argument to retrieve.
     * @param fieldNames   the sequence of nested field names to traverse in order to
     *                     reach the target field.
     * @param <T>          the expected return type of the argument value.
     * @return the value of the specified argument if found; otherwise, null.
     * How it works:
     * <ul>
     *   <li>Starts from the current field ({@code this}).</li>
     *   <li>Iteratively navigates through the field names provided in {@code fieldNames}.</li>
     *   <li>If all fields in the path exist, retrieves the argument value
     *   from the final field.</li>
     *   <li>Returns {@code null} if any field in the path is missing.</li>
     * </ul>
     *
     */
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

    /**
     * Retrieves the value of a specified argument from a nested GraphQL field path
     * and transforms it into the desired type.
     *
     * @param argumentName the name of the argument to retrieve.
     * @param type         the target class type to which the argument value should be converted.
     * @param fieldNames   the sequence of nested field names to traverse in order to reach
     *                     the target field.
     * @param <T>          the expected return type.
     * @return the transformed value of the specified argument if found and convertible;
     *         otherwise, null.
     * How it works:
     * - Delegates to {@link #getFieldArgumentValue(String, String...)} to retrieve
     *   the raw argument value.
     * - If the value is non-null, uses {@code EzySingletonOutputTransformer} to transform
     *   it into the specified type.
     * - Returns null if the argument is not found or the value is null.
     *
     * @throws ClassCastException if the transformation result cannot be cast
     *         to the specified type.
     */
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
        StringBuilder builder = new StringBuilder()
            .append(name);
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
