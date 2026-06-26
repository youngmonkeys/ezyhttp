package com.tvd12.ezyhttp.server.graphql.data;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLInvalidSchemeException;
import lombok.AllArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tvd12.ezyhttp.server.graphql.constants.GraphQLConstants.ALL_FIELDS;

public class GraphQLDataFilter {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map filter(
        Map data,
        GraphQLField queryDefinition
    ) {
        Map answer = new HashMap<>();
        Deque<StackEntry> stack = new ArrayDeque<>();
        stack.push(new StackEntry(queryDefinition, data, answer));
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
                    pushListItems(stack, (List) value, field, newList);
                } else {
                    throw new GraphQLInvalidSchemeException(
                        EzyMapBuilder.mapBuilder()
                            .put("schema", "invalid")
                            .put("field", fieldName)
                            .toMap()
                    );
                }
            }
        }
        return answer;
    }

    @SuppressWarnings({"rawtypes"})
    public List<Map> filterList(
        List<Map> dataList,
        GraphQLField queryDefinition
    ) {
        List<Map> answer = new LinkedList<>();
        Deque<StackEntry> stack = new ArrayDeque<>();
        pushListItems(stack, dataList, queryDefinition, answer);
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
                    pushListItems(stack, (List) value, field, newList);
                } else {
                    throw new GraphQLInvalidSchemeException(
                        EzyMapBuilder.mapBuilder()
                            .put("schema", "invalid")
                            .put("field", fieldName)
                            .toMap()
                    );
                }
            }
        }
        return answer;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void pushListItems(
        Deque<StackEntry> stack,
        List<Map> dataList,
        GraphQLField queryDefinition,
        List<Map> answer
    ) {
        int size = dataList.size();
        for (int i = size - 1; i >= 0; --i) {
            Map item = new HashMap<>();
            answer.add(0, item);
            stack.push(new StackEntry(queryDefinition, dataList.get(i), item));
        }
    }

    @AllArgsConstructor
    @SuppressWarnings("rawtypes")
    private static class StackEntry {
        private GraphQLField field;
        private Map data;
        private Map output;
    }
}
