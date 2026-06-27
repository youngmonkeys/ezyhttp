package com.tvd12.ezyhttp.server.graphql.data;

import com.tvd12.ezyhttp.server.graphql.exception.GraphQLInvalidSchemeException;
import lombok.AllArgsConstructor;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tvd12.ezyhttp.server.graphql.constants.GraphQLConstants.ALL_FIELDS;

public class GraphQLDataFilter {

    @SuppressWarnings("rawtypes")
    public Map filter(
        Map data,
        GraphQLField queryDefinition
    ) {
        Map answer = new HashMap<>();
        Deque<StackEntry> stack = new ArrayDeque<>();
        stack.push(new StackEntry(queryDefinition, data, answer));
        filterStack(stack);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    public List<Map> filterList(
        List<Map> dataList,
        GraphQLField queryDefinition
    ) {
        List<Map> answer = new LinkedList<>();
        Deque<StackEntry> stack = new ArrayDeque<>();
        pushListItems(
            stack,
            dataList,
            queryDefinition,
            answer,
            queryDefinition.getName()
        );
        filterStack(stack);
        return answer;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void filterStack(Deque<StackEntry> stack) {
        while (!stack.isEmpty()) {
            StackEntry entry = stack.pop();

            GraphQLField allField = entry.field.getField(ALL_FIELDS);
            if (allField != null && entry.data != null) {
                Set<Map.Entry> entries = entry.data.entrySet();
                for (Map.Entry e : entries) {
                    Object v = e.getValue();
                    if (v != null) {
                        entry.output.put(e.getKey(), v);
                    }
                }
            }

            for (GraphQLField field : entry.field.getFields()) {
                String fieldName = field.getName();
                if (entry.data == null) {
                    continue;
                }
                Object value = entry.data.get(fieldName);
                if (value == null) {
                    continue;
                }
                if (field.getFields().isEmpty()) {
                    entry.output.put(fieldName, value);
                    continue;
                }
                if (value instanceof Map) {
                    Map newItem = new HashMap<>();
                    entry.output.put(fieldName, newItem);
                    stack.push(new StackEntry(field, (Map) value, newItem));
                } else if (value instanceof List) {
                    List<Map> newList = new LinkedList<>();
                    entry.output.put(fieldName, newList);
                    pushListItems(
                        stack,
                        (List) value,
                        field,
                        newList,
                        fieldName
                    );
                } else {
                    throw newInvalidSchemeException(fieldName);
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void pushListItems(
        Deque<StackEntry> stack,
        List dataList,
        GraphQLField queryDefinition,
        List<Map> answer,
        String fieldName
    ) {
        int size = dataList.size();
        for (int i = size - 1; i >= 0; --i) {
            Object data = dataList.get(i);
            if (!(data instanceof Map)) {
                throw newInvalidSchemeException(fieldName);
            }
            Map item = new HashMap<>();
            answer.add(0, item);
            stack.push(new StackEntry(queryDefinition, (Map) data, item));
        }
    }

    private GraphQLInvalidSchemeException newInvalidSchemeException(
        String fieldName
    ) {
        return new GraphQLInvalidSchemeException(
            Collections.singletonList(
                GraphQLError.builder()
                    .message("invalid schema for field: " + fieldName)
                    .build()
            )
        );
    }

    @AllArgsConstructor
    @SuppressWarnings("rawtypes")
    private static class StackEntry {
        private GraphQLField field;
        private Map data;
        private Map output;
    }
}
