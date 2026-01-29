package com.tvd12.ezyhttp.server.graphql.scheme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLField;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLObjectMapperException;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;
import lombok.AllArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;

@AllArgsConstructor
public final class GraphQLSchemaParser {

    private final ObjectMapper objectMapper;

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
                GraphQLField.Builder childBuilder = stack.peek();
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
                    childBuilder.arguments(
                        objectMapper.readValue(
                            arguments,
                            Map.class
                        )
                    );
                } catch (Exception e) {
                    throw new GraphQLObjectMapperException(
                        EzyMapBuilder.mapBuilder()
                            .put("arguments", "invalid")
                            .put("message", e.getMessage())
                            .toMap(),
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

                GraphQLField.Builder parentBuilder = stack.peek();
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

                GraphQLField.Builder parentBuilder = stack.peek();
                parentBuilder.addField(childBuilder.build());

                GraphQLField.Builder newChildBuilder = GraphQLField.builder();
                stack.push(newChildBuilder);
            } else {
                nameBuffer[nameLength++] = ch;
            }
        }
        return schemaBuilder.build();
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
        String trimedQuery = query.trim();
        StringBuilder forwardStandard = forwardStandardize(trimedQuery);
        StringBuilder backwardStandard = backwardStandardize(
            forwardStandard.toString()
        );
        return removeQueryPrefix(backwardStandard.toString());
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
        if (s.startsWith(prefix)) {
            return s.substring(prefix.length());
        }
        return s;
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
                    if (ch == ' ') {
                        continue;
                    }
                    if (ch != ',' && ch != ')' && ch != '}') {
                        varNameBuilder.append(ch);
                    } else {
                        --i;
                        break;
                    }
                }
                String varName = varNameBuilder.toString();
                Object value = variables.get(varName);
                if (value instanceof String) {
                    value = "\"" + value + "\"";
                }
                argumentsBuilder.append(value);
                continue;
            }
            argumentsBuilder.append(ch);
        }
        return i;
    }
}
