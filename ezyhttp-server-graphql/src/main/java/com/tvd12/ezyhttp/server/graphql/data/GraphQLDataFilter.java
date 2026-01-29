package com.tvd12.ezyhttp.server.graphql.data;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLInvalidSchemeException;
import lombok.AllArgsConstructor;

import java.util.*;

import static com.tvd12.ezyhttp.server.graphql.constants.GraphQLConstants.ALL_FIELDS;

public class GraphQLDataFilter {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map filter(
        Map data,
        GraphQLField queryDefinition
    ) {
        Map answer = new HashMap<>();
        Map parentMap = null;
        Deque<StackEntry> stack = new ArrayDeque<>();
        stack.push(new StackEntry(queryDefinition, data));
        while (!stack.isEmpty()) {
            StackEntry entry = stack.pop();
            String parentName = entry.field.getName();
            parentMap = parentMap == null
                ? answer
                : (Map) parentMap.get(parentName);

            GraphQLField allField = entry.field.getField(ALL_FIELDS);
            if (allField != null) {
                Set<Map.Entry> entries = entry.data.entrySet();
                for (Map.Entry e : entries) {
                    Object v = e.getValue();
                    if (v != null) {
                        parentMap.put(e.getKey(), v);
                    }
                }
            }

            for (GraphQLField field : entry.field.getFields()) {
                String fieldName = field.getName();
                Object value = entry.data.get(fieldName);
                if (value == null) {
                    continue;
                }
                if (field.getFields().isEmpty()) {
                    parentMap.put(fieldName, value);
                    continue;
                }
                if (value instanceof Map) {
                    Object newItem = new HashMap<>();
                    parentMap.put(fieldName, newItem);
                    stack.push(new StackEntry(field, (Map) value));
                } else if (value instanceof List) {
                    parentMap.put(
                        fieldName,
                        filterList((List) value, field)
                    );
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
        for (Map map : dataList) {
            answer.add(filter(map, queryDefinition));
        }
        return answer;
    }

    @AllArgsConstructor
    @SuppressWarnings("rawtypes")
    private static class StackEntry {
        private GraphQLField field;
        private Map data;
    }
}
