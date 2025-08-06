package com.tvd12.ezyhttp.server.graphql.util;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static com.tvd12.ezyhttp.core.constant.Constants.DEFAULT_QL_GROUP_NAME;

public final class GraphQLQueryGroupExtractors {

    private GraphQLQueryGroupExtractors() {}

    public static String extractQueryGroup(String queryName) {
        String group = DEFAULT_QL_GROUP_NAME;
        if (isNotBlank(queryName)) {
            int dotIndex = queryName.indexOf('.');
            if (dotIndex > 0) {
                group = queryName.substring(0, dotIndex);
            }
        }
        return group;
    }
}
