package com.tvd12.ezyhttp.server.graphql.scheme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLError;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLField;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLObjectMapperException;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;
import lombok.AllArgsConstructor;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;

@AllArgsConstructor
public final class GraphQLSchemaParser {

    private final ObjectMapper objectMapper;

    private static final String VARIABLE_PLACEHOLDER_FIELD =
        "__ezyhttp_graphql_variable__";

    public GraphQLSchema parseQuery(
        String queryToParse,
        String operationName,
        Map<String, Object> variables
    ) {
        if (!isNotBlank(operationName)) {
            return parseQuery(queryToParse, variables);
        }
        String standardized = standardizeKeepOperationNames(queryToParse);
        String selectionSet = extractNamedOperation(standardized, operationName);
        if (selectionSet == null) {
            throw new GraphQLObjectMapperException(
                Collections.singletonList(
                    GraphQLError.builder()
                        .message("unknown operation named: " + operationName)
                        .build()
                )
            );
        }
        return parseQuery(selectionSet, variables);
    }

    @SuppressWarnings({"unchecked", "MethodLength"})
    public GraphQLSchema parseQuery(
        String queryToParse,
        Map<String, Object> variables
    ) {
        String query = standardize(queryToParse);

        Deque<GraphQLField.Builder> stack = new ArrayDeque<>();
        GraphQLSchema.Builder schemaBuilder = GraphQLSchema.builder();

        int queryLength = query.length();
        int nameLength = 0;
        char[] nameBuffer = new char[128];
        for (int i = 0; i < queryLength; ++i) {
            char ch = query.charAt(i);
            if (ch == '{') {
                if (stack.isEmpty()) {
                    GraphQLQueryDefinition.Builder queryBuilder =
                        GraphQLQueryDefinition.builder();
                    stack.push(queryBuilder);
                    continue;
                }

                GraphQLField.Builder builder = stack.peek();
                if (nameLength > 0) {
                    builder.name(String.copyValueOf(nameBuffer, 0, nameLength));
                    nameLength = 0;
                }
                GraphQLField.Builder childBuilder = GraphQLField.builder();
                stack.push(childBuilder);
                continue;
            }

            if (ch == '(') {
                GraphQLField.Builder childBuilder = peekFieldStackItemOrThrow(
                    stack,
                    "there is no child"
                );
                StringBuilder argumentsBuilder = new StringBuilder();
                i = extractQueryArguments(
                    argumentsBuilder,
                    query,
                    i,
                    queryLength,
                    variables
                );
                String arguments = "{" + argumentsBuilder + "}";
                try {
                    Map<String, Object> argumentMap = objectMapper.readValue(
                        arguments,
                        Map.class
                    );
                    replaceVariablePlaceholders(argumentMap, variables);
                    childBuilder.arguments(argumentMap);
                } catch (Exception e) {
                    throw new GraphQLObjectMapperException(
                        Collections.singletonList(
                            GraphQLError.builder()
                                .message("invalid arguments: " + e.getMessage())
                                .build()
                        ),
                        e
                    );
                }
                continue;
            }

            if (ch == '}') {
                if (stack.isEmpty()) {
                    continue;
                }
                if (stack.size() == 1) {
                    GraphQLField.Builder item = stack.pop();
                    if (nameLength > 0) {
                        item.name(
                            String.copyValueOf(nameBuffer, 0, nameLength)
                        );
                        nameLength = 0;
                    }
                    schemaBuilder.addQueryDefinition(
                        (GraphQLQueryDefinition) item.build()
                    );
                    continue;
                }

                GraphQLField.Builder childBuilder = stack.pop();
                if (nameLength > 0) {
                    childBuilder.name(
                        String.copyValueOf(nameBuffer, 0, nameLength)
                    );
                    nameLength = 0;
                }

                GraphQLField.Builder parentBuilder = peekFieldStackItemOrThrow(
                    stack,
                    "there is no parent case curly brace close"
                );
                parentBuilder.addField(childBuilder.build());

                if (stack.size() == 1) {
                    GraphQLField.Builder item = stack.pop();
                    schemaBuilder.addQueryDefinition(
                        (GraphQLQueryDefinition) item.build()
                    );
                }
            } else if (ch == ' ') { // ',' '\t' '\n' '+' have been removed
                if (stack.isEmpty()) {
                    GraphQLQueryDefinition.Builder queryBuilder =
                        GraphQLQueryDefinition.builder();
                    stack.push(queryBuilder);
                    nameLength = 0;
                    continue;
                }

                if (stack.size() == 1) {
                    GraphQLField.Builder item = stack.pop();
                    item.name(String.copyValueOf(nameBuffer, 0, nameLength));
                    nameLength = 0;
                    schemaBuilder.addQueryDefinition(
                        (GraphQLQueryDefinition) item.build()
                    );

                    GraphQLQueryDefinition.Builder queryBuilder =
                        GraphQLQueryDefinition.builder();
                    stack.push(queryBuilder);
                    continue;
                }

                GraphQLField.Builder childBuilder = stack.pop();
                if (nameLength > 0) {
                    childBuilder.name(
                        String.copyValueOf(nameBuffer, 0, nameLength)
                    );
                    nameLength = 0;
                }

                GraphQLField.Builder parentBuilder = peekFieldStackItemOrThrow(
                    stack,
                    "there is no parent case space"
                );
                parentBuilder.addField(childBuilder.build());

                GraphQLField.Builder newChildBuilder = GraphQLField.builder();
                stack.push(newChildBuilder);
            } else {
                nameBuffer[nameLength++] = ch;
            }
        }
        return schemaBuilder.build();
    }

    private String extractNamedOperation(
        String standardizedQuery,
        String operationName
    ) {
        int nameIdx = standardizedQuery.indexOf(operationName);
        while (nameIdx >= 0) {
            boolean validPrefix = nameIdx == 0
                || !isGraphQLNameChar(standardizedQuery.charAt(nameIdx - 1));
            int afterName = nameIdx + operationName.length();
            boolean validSuffix = afterName >= standardizedQuery.length()
                || !isGraphQLNameChar(standardizedQuery.charAt(afterName));
            if (validPrefix && validSuffix) {
                int braceStart = afterName;
                while (braceStart < standardizedQuery.length()
                    && standardizedQuery.charAt(braceStart) != '{') {
                    braceStart++;
                }
                if (braceStart < standardizedQuery.length()) {
                    int depth = 0;
                    for (int i = braceStart; i < standardizedQuery.length(); i++) {
                        char c = standardizedQuery.charAt(i);
                        if (c == '{') {
                            depth++;
                        } else if (c == '}') {
                            if (--depth == 0) {
                                return standardizedQuery.substring(braceStart, i + 1);
                            }
                        }
                    }
                }
            }
            nameIdx = standardizedQuery.indexOf(operationName, nameIdx + 1);
        }
        return null;
    }

    /**
     * Remove redundant '\t', '\n', '+', ',', ' ' in query.
     *
     * @param query query in original format
     * @return standardized query
     */
    private String standardize(String query) {
        if (query == null) {
            return EMPTY_STRING;
        }
        String trimmedQuery = query.trim();
        StringBuilder forwardStandard = forwardStandardize(trimmedQuery);
        StringBuilder backwardStandard = backwardStandardize(
            forwardStandard.toString()
        );
        return removeQueryPrefix(backwardStandard.toString());
    }

    private String standardizeKeepOperationNames(String query) {
        if (query == null) {
            return EMPTY_STRING;
        }
        StringBuilder forwardStandard = forwardStandardize(query.trim());
        return backwardStandardize(forwardStandard.toString()).toString();
    }

    private StringBuilder forwardStandardize(String query) {
        int queryLength = query.length();
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < queryLength; ++i) {
            char ch = query.charAt(i);
            if (ch == '(') {
                StringBuilder argumentsBuilder = new StringBuilder();
                i = extractQueryArguments(
                    argumentsBuilder,
                    query,
                    i,
                    queryLength,
                    null
                );
                answer
                    .append('(')
                    .append(argumentsBuilder)
                    .append(')');
                continue;
            }
            if (ch == '{' || ch == '}') {
                answer.append(ch);
            } else if (ch == '+' || ch == ',' || ch == ' ' || ch == '\t' || ch == '\n') {
                if (answer.length() == 0) {
                    continue;
                }
                char lastChar = answer.charAt(answer.length() - 1);
                if ((lastChar != ' ') && (lastChar != '{')) {
                    answer.append(' ');
                }
            } else {
                answer.append(ch);
            }
        }
        return answer;
    }

    private StringBuilder backwardStandardize(String query) {
        int queryLength = query.length();
        StringBuilder answer = new StringBuilder();
        for (int i = queryLength - 1; i >= 0; --i) {
            char ch = query.charAt(i);
            if (ch == '{' || ch == '}') {
                answer.insert(0, ch);
            } else if (ch == ' ') { // ',' '\t' '\n' '+' have been removed in forward pass
                if (answer.length() == 0) {
                    continue;
                }
                char firstChar = answer.charAt(0);
                if ((firstChar != '{') && (firstChar != '}')) {
                    answer.insert(0, ' ');
                }
            } else {
                answer.insert(0, ch);
            }
        }
        return answer;
    }

    private String removeQueryPrefix(String s) {
        String prefix = "query";
        if (s.startsWith(prefix)
            && (s.length() == prefix.length()
            || !isGraphQLNameChar(s.charAt(prefix.length())))
        ) {
            int selectionStart = findOperationSelectionStart(
                s,
                prefix.length()
            );
            return selectionStart >= 0
                ? s.substring(selectionStart)
                : s.substring(prefix.length());
        }
        return s;
    }

    private int findOperationSelectionStart(String s, int start) {
        int parenthesesCount = 0;
        int quoteCount = 0;
        int quotesCount = 0;
        int length = s.length();
        for (int i = start; i < length; ++i) {
            char prevCh = i > 0 ? s.charAt(i - 1) : 0;
            char ch = s.charAt(i);
            if (prevCh != '\\' && ch == '\'') {
                quoteCount = quoteCount == 0 ? 1 : 0;
            } else if (prevCh != '\\' && ch == '"') {
                quotesCount = quotesCount == 0 ? 1 : 0;
            } else if (quoteCount == 0 && quotesCount == 0) {
                if (ch == '(') {
                    ++parenthesesCount;
                } else if (ch == ')') {
                    --parenthesesCount;
                } else if (ch == '{' && parenthesesCount == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int extractQueryArguments(
        StringBuilder argumentsBuilder,
        String query,
        int start,
        int queryLength,
        Map<String, Object> variables
    ) {
        int i = start + 1;
        int quoteCount = 0;
        int quotesCount = 0;
        for (; i < queryLength; ++i) {
            char prevCh = query.charAt(i - 1);
            char ch = query.charAt(i);
            if (prevCh != '\\' && ch == '\'') {
                quoteCount = quoteCount == 0 ? 1 : 0;
            } else if (prevCh != '\\' && ch == '"') {
                quotesCount = quotesCount == 0 ? 1 : 0;
            } else if (ch == ')' && quoteCount == 0 && quotesCount == 0) {
                break;
            } else if (variables != null && ch == '$') {
                StringBuilder varNameBuilder = new StringBuilder();
                for (++i; i < queryLength; ++i) {
                    ch = query.charAt(i);
                    if (isGraphQLNameChar(ch)) {
                        varNameBuilder.append(ch);
                    } else {
                        --i;
                        break;
                    }
                }
                argumentsBuilder
                    .append("{\"")
                    .append(VARIABLE_PLACEHOLDER_FIELD)
                    .append("\":\"")
                    .append(varNameBuilder)
                    .append("\"}");
                continue;
            }
            argumentsBuilder.append(ch);
        }
        return i;
    }

    @SuppressWarnings("unchecked")
    private void replaceVariablePlaceholders(
        Map<String, Object> arguments,
        Map<String, Object> variables
    ) {
        if (arguments == null || variables == null) {
            return;
        }
        Deque<Object> stack = new ArrayDeque<>();
        stack.push(arguments);
        while (!stack.isEmpty()) {
            Object item = stack.pop();
            if (item instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) item;
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object value = entry.getValue();
                    if (isVariablePlaceholder(value)) {
                        entry.setValue(getVariableValue(value, variables));
                    } else if (value instanceof Map || value instanceof List) {
                        stack.push(value);
                    }
                }
            } else {
                List<Object> list = (List<Object>) item;
                int size = list.size();
                for (int i = 0; i < size; ++i) {
                    Object value = list.get(i);
                    if (isVariablePlaceholder(value)) {
                        list.set(i, getVariableValue(value, variables));
                    } else if (value instanceof Map || value instanceof List) {
                        stack.push(value);
                    }
                }
            }
        }
    }

    private boolean isVariablePlaceholder(Object value) {
        return value instanceof Map
            && ((Map<?, ?>) value).size() == 1
            && ((Map<?, ?>) value).containsKey(VARIABLE_PLACEHOLDER_FIELD);
    }

    private Object getVariableValue(
        Object placeholder,
        Map<String, Object> variables
    ) {
        Object variableName = ((Map<?, ?>) placeholder).get(
            VARIABLE_PLACEHOLDER_FIELD
        );
        return variableName instanceof String
            ? variables.get(variableName)
            : null;
    }

    private boolean isGraphQLNameChar(char ch) {
        return (ch >= 'A' && ch <= 'Z')
            || (ch >= 'a' && ch <= 'z')
            || (ch >= '0' && ch <= '9')
            || ch == '_';
    }

    private GraphQLField.Builder peekFieldStackItemOrThrow(
        Deque<GraphQLField.Builder> stack,
        String message
    ) {
        GraphQLField.Builder parentBuilder = stack.peek();
        if (parentBuilder == null) {
            throw new GraphQLObjectMapperException(
                Collections.singletonList(
                    GraphQLError.builder()
                        .message(message)
                        .build()
                )
            );
        }
        return parentBuilder;
    }
}
