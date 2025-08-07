package com.tvd12.ezyhttp.server.graphql.scheme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyhttp.server.graphql.data.GraphQLField;
import com.tvd12.ezyhttp.server.graphql.exception.GraphQLObjectMapperException;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Stack;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static com.tvd12.ezyhttp.server.graphql.constants.GraphQLConstants.PREFIX_QUERY;
import static java.util.Collections.singletonMap;

@AllArgsConstructor
public final class GraphQLSchemaParser {

    private final ObjectMapper objectMapper;

    @SuppressWarnings({"unchecked", "MethodLength"})
    public GraphQLSchema parseQuery(
        String queryToParse,
        Map<String, Object> variables
    ) {
        String query = queryToParse;
        if (query == null) {
            query = EMPTY_STRING;
        }

        int i = 0;
        if (query.startsWith(PREFIX_QUERY)) {
            i = PREFIX_QUERY.length();
        }

        Stack<GraphQLField.Builder> stack = new Stack<>();
        GraphQLSchema.Builder schemaBuilder = GraphQLSchema.builder();

        int queryLength = query.length();
        i = ignoreSpaceAndPlus(query, i, queryLength);

        int nameLength = 0;
        char[] nameBuffer = new char[128];
        for (; i < queryLength; ++i) {
            char ch = query.charAt(i);
            if (ch == '{') {
                if (stack.isEmpty()) {
                    GraphQLQueryDefinition.Builder queryBuilder =
                        GraphQLQueryDefinition.builder();
                    stack.add(queryBuilder);
                    continue;
                }

                GraphQLField.Builder builder = stack.peek();
                if (nameLength > 0) {
                    builder.name(String.copyValueOf(nameBuffer, 0, nameLength));
                    nameLength = 0;
                }
                GraphQLField.Builder childBuilder = GraphQLField.builder();
                stack.add(childBuilder);
                continue;
            }

            if (ch == '(') {
                StringBuilder builder = new StringBuilder("{");
                Stack<Character> stateStack = new Stack<>();
                for (++i; i < queryLength; ++i) {
                    ch = query.charAt(i);
                    char prevCh = query.charAt(i - 1);
                    if (ch == '"' && prevCh != '\\') {
                        if (stateStack.isEmpty()) {
                            stateStack.push(ch);
                        } else {
                            stateStack.pop();
                        }
                    }
                    if (stateStack.isEmpty()) {
                        if (ch == ')') {
                            builder.append("}");
                            break;
                        } else if (ch == '$') {
                            StringBuilder varNameBuilder = new StringBuilder();
                            for (++i; i < queryLength; ++i) {
                                ch = query.charAt(i);
                                if (ch == ' ') {
                                    continue;
                                }
                                if (ch != ',' && ch != ')') {
                                    varNameBuilder.append(ch);
                                } else {
                                    --i;
                                    break;
                                }
                            }
                            String varName = varNameBuilder.toString();
                            Object value = variables.get(varName);
                            if (value != null) {
                                if (value instanceof String) {
                                    builder
                                        .append("\"")
                                        .append(value)
                                        .append("\"");
                                } else {
                                    builder.append(value);
                                }
                            }
                        }
                    }
                    builder.append(ch);
                }
                if (stack.isEmpty()) {
                    continue;
                }
                GraphQLField.Builder childBuilder = stack.peek();
                try {
                    childBuilder.argumentName(
                        objectMapper.readValue(
                            builder.toString(),
                            Map.class
                        )
                    );
                } catch (Exception e) {
                    throw new GraphQLObjectMapperException(
                        singletonMap("arguments", "invalid"),
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
                        item.name(String.copyValueOf(nameBuffer, 0, nameLength));
                        nameLength = 0;
                    }
                    schemaBuilder.addQueryDefinition((GraphQLQueryDefinition) item.build());
                    continue;
                }

                GraphQLField.Builder childBuilder = stack.pop();
                if (nameLength > 0) {
                    childBuilder.name(String.copyValueOf(nameBuffer, 0, nameLength));
                    nameLength = 0;
                }

                GraphQLField.Builder parentBuilder = stack.peek();
                parentBuilder.addField(childBuilder.build());

                if (stack.size() == 1) {
                    GraphQLField.Builder item = stack.pop();
                    schemaBuilder.addQueryDefinition((GraphQLQueryDefinition) item.build());
                }
            } else if (ch == ' ') { // ',' '\t' '\n' '+' have been removed
                if (stack.isEmpty()) {
                    GraphQLQueryDefinition.Builder queryBuilder = GraphQLQueryDefinition.builder();
                    stack.add(queryBuilder);
                    nameLength = 0;
                    continue;
                }

                if (stack.size() == 1) {
                    GraphQLField.Builder item = stack.pop();
                    item.name(String.copyValueOf(nameBuffer, 0, nameLength));
                    nameLength = 0;
                    schemaBuilder.addQueryDefinition((GraphQLQueryDefinition) item.build());

                    GraphQLQueryDefinition.Builder queryBuilder = GraphQLQueryDefinition.builder();
                    stack.add(queryBuilder);
                    continue;
                }

                GraphQLField.Builder childBuilder = stack.pop();
                if (nameLength > 0) {
                    childBuilder.name(String.copyValueOf(nameBuffer, 0, nameLength));
                    nameLength = 0;
                }

                GraphQLField.Builder parentBuilder = stack.peek();
                parentBuilder.addField(childBuilder.build());

                GraphQLField.Builder newChildBuilder = GraphQLField.builder();
                stack.add(newChildBuilder);
            } else {
                nameBuffer[nameLength++] = ch;
            }
        }
        return schemaBuilder.build();
    }

    private int ignoreSpace(
        String query,
        int start,
        int queryLength
    ) {
        int i = start;
        for (; i < queryLength; ++i) {
            char ch = query.charAt(i);
            if (ch != ' ' && ch != '\t' && ch != '\n') {
                break;
            }
        }
        return i;
    }

    private int ignoreSpaceAndPlus(
        String query,
        int start,
        int queryLength
    ) {
        int i = start;
        for (; i < queryLength; ++i) {
            char ch = query.charAt(i);
            if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '+') {
                break;
            }
        }
        return i;
    }

    /**
     * Remove redundant '\t', '\n', '+', ',', ' ' in query.
     *
     * @param query query in original format
     * @return standardized query
     */
    private String standardize(String query) {
        if (query == null) {
            return "";
        }
        String trimedQuery = query.trim();
        //StringBuilder forwardStandard = forwardStandardize(trimedQuery);
        //StringBuilder backwardStandard = backwardStandardize(forwardStandard.toString());
        return trimedQuery;
    }

    private StringBuilder forwardStandardize(String query) {
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < query.length(); ++i) {
            char ch = query.charAt(i);
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
        StringBuilder answer = new StringBuilder();
        for (int i = query.length() - 1; i >= 0; --i) {
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
}
